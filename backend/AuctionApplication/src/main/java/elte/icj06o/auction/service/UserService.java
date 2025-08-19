package elte.icj06o.auction.service;

import elte.icj06o.auction.dto.*;
import elte.icj06o.auction.model.Country;
import elte.icj06o.auction.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface UserService {
    void register(User user) throws Exception;
    Page<User> getAllFilteredUsers(String title, Country country, Pageable pageable);
    Optional<User> getUserById(Long id);
    Optional<User> getUserByEmail(String email);
    User updateUser(Long id, User user);
    User updateUserPassword(User user, String password);
    User updateUserProfilePicture(User user, String profilePicture);
    Boolean checkPasswords(String password, String encryptedPassword);
    List<User> getFollowings(User user);
    List<User> getFollowers(User user);
    Set<Long> followUser(User follower, User following);
    AuthUser userToAuthUser(User user);
    UserBadge userToUserBadge(User user);
    UserList userToUserList(User user);
}
