package elte.icj06o.auction.service;

import elte.icj06o.auction.dto.*;
import elte.icj06o.auction.model.Auction;
import elte.icj06o.auction.model.Country;
import elte.icj06o.auction.model.Role;
import elte.icj06o.auction.model.User;
import elte.icj06o.auction.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(User user) throws Exception {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new Exception(user.getEmail() + " is already registered.");
        }

        user.setId(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getRoles().add(Role.USER);
        userRepository.save(user);
    }

    public Page<User> getAllFilteredUsers(String title, Country country, Pageable pageable) {
        return userRepository.findFilteredUsers(title, country, pageable);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User updateUser(Long id, User user) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.setFirstName(user.getFirstName());
            existingUser.setMiddleName(user.getMiddleName());
            existingUser.setLastName(user.getLastName());
            existingUser.setDateOfBirth(user.getDateOfBirth());
            existingUser.setLocation(user.getLocation());
            return userRepository.save(existingUser);
        } else {
            throw new RuntimeException("User not found with id: " + user.getId());
        }
    }

    public User updateUserProfilePicture(User user, String profilePicture) {
        user.setProfilePicture(profilePicture);
        return userRepository.save(user);
    }

    public User updateUserPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public Boolean checkPasswords(String password, String encryptedPassword) {
        return passwordEncoder.matches(password, encryptedPassword);
    }

    public List<User> getFollowings(User user) {
        return user.getFollowings().stream().toList();
    }

    public List<User> getFollowers(User user) {
        return user.getFollowers().stream().toList();
    }

    public Set<Long> followUser(User follower, User following) {
        if (!follower.getFollowings().contains(following)) {
            follower.getFollowings().add(following);
            following.getFollowers().add(follower);
        } else {
            follower.getFollowings().remove(following);
            following.getFollowers().remove(follower);
        }
        userRepository.save(follower);
        userRepository.save(following);
        return new HashSet<>(following.getFollowers().stream().map(User::getId).toList());
    }

    public AuthUser userToAuthUser(User user) {
        return new AuthUser(
                user.getId(),
                user.getFirstName(),
                user.getMiddleName(),
                user.getLastName(),
                user.getEmail(),
                user.getDateOfBirth(),
                user.getLocation().getDisplayName(),
                user.getProfilePicture()
        );
    }

    public UserBadge userToUserBadge(User user) {
        return new UserBadge(
                user.getId(),
                user.getFirstName(),
                user.getMiddleName(),
                user.getLastName(),
                user.getProfilePicture()
        );
    }

    public UserList userToUserList(User user) {
        return new UserList(
                user.getId(),
                user.getFirstName(),
                user.getMiddleName(),
                user.getLastName(),
                user.getLocation().getDisplayName(),
                user.getProfilePicture(),
                user.getFollowers().stream().map(User::getId).toList(),
                user.getFollowings().stream().map(User::getId).toList(),
                user.getCreatedAuctions().stream()
                        .filter(auction ->
                                auction.getStartDate().before(new Date())
                                && auction.getEndDate().after(new Date())
                                && auction.getWinner() == null)
                        .map(Auction::getId)
                        .toList(),
                user.getCreatedAuctions().stream()
                        .filter(auction -> auction.getWinner() != null)
                        .map(Auction::getId)
                        .toList()
        );
    }
}
