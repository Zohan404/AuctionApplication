package elte.icj06o.auction.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import elte.icj06o.auction.dto.AuthUser;
import elte.icj06o.auction.dto.Passwords;
import elte.icj06o.auction.dto.UserList;
import elte.icj06o.auction.model.Country;
import elte.icj06o.auction.model.User;
import elte.icj06o.auction.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final Cloudinary cloudinary;

    public UserController(UserService userService, Cloudinary cloudinary) {
        this.userService = userService;
        this.cloudinary = cloudinary;
    }

    @GetMapping
    public List<UserList> getAllUsers(
            @RequestParam(required = false, defaultValue = "") String title,
            @RequestParam(required = false, defaultValue = "") Country country,
            @RequestParam(defaultValue = "0") int page
            ) {
        Sort.Direction direction = Sort.Direction.fromString("asc");
        Sort sortBy = Sort.by(direction, "id");
        Pageable pageable = PageRequest.of(page, 10, sortBy);
        return userService.getAllFilteredUsers(title, country, pageable)
                .stream()
                .map(userService::userToUserList)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserList> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(userService.userToUserList(user)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<AuthUser> getLoggedInUser(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getUserByEmail(userDetails.getUsername())
                .map(user -> ResponseEntity.ok(userService.userToAuthUser(user)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    public ResponseEntity<AuthUser> updateUser(@Valid @RequestBody User user,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        User loggedInUser = userService.getUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            User updatedUser = userService.updateUser(loggedInUser.getId(), user);
            return ResponseEntity.ok(userService.userToAuthUser(updatedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/me/password")
    public ResponseEntity<?> updateUserPassword(@Valid @RequestBody Passwords passwords,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        if (!userService.checkPasswords(passwords.getOldPassword(), userDetails.getPassword())) {
            throw new RuntimeException("Existing password is incorrect.");
        }

        if (!passwords.getNewPassword().equals(passwords.getConfirmPassword())) {
            throw new RuntimeException("New password and confirmation password do not match.");
        }

        return userService.getUserByEmail(userDetails.getUsername())
                .map(user -> ResponseEntity.ok(userService.updateUserPassword(user, passwords.getNewPassword())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/me/profile-picture")
    public ResponseEntity<AuthUser> updateUserProfilePicture(@RequestParam("file") MultipartFile file,
                                                         @AuthenticationPrincipal UserDetails userDetails)
    throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        String imageUrl = (String) uploadResult.get("secure_url");

        return userService.getUserByEmail(userDetails.getUsername())
                .map(user -> ResponseEntity.ok(userService.userToAuthUser(
                        userService.updateUserProfilePicture(user, imageUrl))))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/followers/{id}")
    public ResponseEntity<List<UserList>> getFollowers(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(value -> ResponseEntity.ok(
                        userService.getFollowers(value)
                                .stream()
                                .map(userService::userToUserList)
                                .toList()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/followings/{id}")
    public ResponseEntity<List<UserList>> getFollowings(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(value -> ResponseEntity.ok(
                        userService.getFollowings(value)
                                .stream()
                                .map(userService::userToUserList)
                                .toList()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/follow/{id}")
    public ResponseEntity<Set<Long>> followUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return userService.getUserByEmail(userDetails.getUsername())
                .map(follower ->
                        userService.getUserById(id)
                                .map(following -> ResponseEntity.ok(userService.followUser(follower, following)))
                                .orElseGet(() -> ResponseEntity.notFound().build())
                )
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
