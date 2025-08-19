package elte.icj06o.auction.service;

import elte.icj06o.auction.dto.AuthUser;
import elte.icj06o.auction.model.Country;
import elte.icj06o.auction.model.Role;
import elte.icj06o.auction.model.User;
import elte.icj06o.auction.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private Date currentDate;
    private Date pastDate;
    private Date futureDate;

    @BeforeEach
    void setUp() {
        currentDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        futureDate = calendar.getTime();

        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        pastDate = calendar.getTime();

        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setMiddleName(null);
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("plainPassword");
        user.setDateOfBirth(pastDate);
        user.setLocation(Country.United_States);
        user.setProfilePicture("https://example.com/profile.jpg");
        user.setRoles(new HashSet<>(Collections.singletonList(Role.USER)));
        user.setFollowers(new HashSet<>());
        user.setFollowings(new HashSet<>());
        user.setCreatedAuctions(new ArrayList<>());
    }

    @Test
    void register_ShouldCreateNewUser() throws Exception {
        String encodedPassword = "encodedPassword";
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.register(user);

        verify(passwordEncoder).encode("plainPassword");
        verify(userRepository).save(user);
        assertTrue(user.getRoles().contains(Role.USER));
        assertEquals(encodedPassword, user.getPassword());
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Exception exception = assertThrows(Exception.class, () -> userService.register(user));
        assertEquals(user.getEmail() + " is already registered.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getAllFilteredUsers_ShouldReturnFilteredUsers() {
        Pageable pageable = Pageable.unpaged();
        Country country = Country.United_States;
        String title = "John";
        List<User> userList = Collections.singletonList(user);
        Page<User> userPage = new PageImpl<>(userList);

        when(userRepository.findFilteredUsers(title, country, pageable)).thenReturn(userPage);

        Page<User> result = userService.getAllFilteredUsers(title, country, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(user, result.getContent().get(0));
        verify(userRepository).findFilteredUsers(title, country, pageable);
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void getUserById_ShouldReturnEmpty_WhenUserDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void getUserByEmail_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserByEmail("john.doe@example.com");

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void updateUser_ShouldUpdateUserDetails() {
        User updatedUser = new User();
        updatedUser.setFirstName("John");
        updatedUser.setMiddleName("M");
        updatedUser.setLastName("Smith");
        updatedUser.setDateOfBirth(pastDate);
        updatedUser.setLocation(Country.Canada);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUser(1L, updatedUser);

        assertEquals("John", result.getFirstName());
        assertEquals("M", result.getMiddleName());
        assertEquals("Smith", result.getLastName());
        assertEquals(Country.Canada, result.getLocation());
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.updateUser(99L, user));
    }

    @Test
    void updateUserProfilePicture_ShouldUpdateProfilePicture() {
        String newProfilePicture = "https://example.com/new_profile.jpg";
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUserProfilePicture(user, newProfilePicture);

        assertEquals(newProfilePicture, result.getProfilePicture());
        verify(userRepository).save(user);
    }

    @Test
    void updateUserPassword_ShouldUpdatePassword() {
        String newPassword = "newPassword";
        String encodedPassword = "encodedNewPassword";
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUserPassword(user, newPassword);

        assertEquals(encodedPassword, result.getPassword());
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(user);
    }

    @Test
    void checkPasswords_ShouldReturnTrue_WhenPasswordsMatch() {
        String plainPassword = "password";
        String encodedPassword = "encodedPassword";
        when(passwordEncoder.matches(plainPassword, encodedPassword)).thenReturn(true);

        boolean result = userService.checkPasswords(plainPassword, encodedPassword);

        assertTrue(result);
    }

    @Test
    void checkPasswords_ShouldReturnFalse_WhenPasswordsDontMatch() {
        String plainPassword = "password";
        String encodedPassword = "encodedPassword";
        when(passwordEncoder.matches(plainPassword, encodedPassword)).thenReturn(false);

        boolean result = userService.checkPasswords(plainPassword, encodedPassword);

        assertFalse(result);
    }

    @Test
    void getFollowings_ShouldReturnListOfFollowings() {
        User following1 = new User();
        following1.setId(2L);
        User following2 = new User();
        following2.setId(3L);

        Set<User> followings = new HashSet<>();
        followings.add(following1);
        followings.add(following2);
        user.setFollowings(followings);

        List<User> result = userService.getFollowings(user);

        assertEquals(2, result.size());
        assertTrue(result.contains(following1));
        assertTrue(result.contains(following2));
    }

    @Test
    void getFollowers_ShouldReturnListOfFollowers() {
        User follower1 = new User();
        follower1.setId(2L);
        User follower2 = new User();
        follower2.setId(3L);

        Set<User> followers = new HashSet<>();
        followers.add(follower1);
        followers.add(follower2);
        user.setFollowers(followers);

        List<User> result = userService.getFollowers(user);

        assertEquals(2, result.size());
        assertTrue(result.contains(follower1));
        assertTrue(result.contains(follower2));
    }

    @Test
    void followUser_ShouldAddFollowing_WhenNotAlreadyFollowing() {
        User follower = new User();
        follower.setId(1L);
        follower.setFollowings(new HashSet<>());

        User following = new User();
        following.setId(2L);
        following.setFollowers(new HashSet<>());

        when(userRepository.save(any(User.class))).thenReturn(null);

        Set<Long> result = userService.followUser(follower, following);

        assertTrue(follower.getFollowings().contains(following));
        assertTrue(following.getFollowers().contains(follower));
        verify(userRepository).save(follower);
        verify(userRepository).save(following);
    }

    @Test
    void followUser_ShouldRemoveFollowing_WhenAlreadyFollowing() {
        User follower = new User();
        follower.setId(1L);

        User following = new User();
        following.setId(2L);

        Set<User> followings = new HashSet<>();
        followings.add(following);
        follower.setFollowings(followings);

        Set<User> followers = new HashSet<>();
        followers.add(follower);
        following.setFollowers(followers);

        when(userRepository.save(any(User.class))).thenReturn(null);

        Set<Long> result = userService.followUser(follower, following);

        assertFalse(follower.getFollowings().contains(following));
        assertFalse(following.getFollowers().contains(follower));
        verify(userRepository).save(follower);
        verify(userRepository).save(following);
    }

    @Test
    void userToAuthUser_ShouldConvertToDto() {
        AuthUser result = userService.userToAuthUser(user);

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getMiddleName(), result.getMiddleName());
        assertEquals(user.getLastName(), result.getLastName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getDateOfBirth(), result.getDateOfBirth());
        assertEquals(user.getLocation().getDisplayName(), result.getLocation());
        assertEquals(user.getProfilePicture(), result.getProfilePicture());
    }
}