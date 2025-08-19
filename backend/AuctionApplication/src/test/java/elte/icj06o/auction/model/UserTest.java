package elte.icj06o.auction.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void testValidUser() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        User user = new User(
                "John",
                null,
                "Doe",
                "password123",
                "john.doe@example.com",
                new Date(System.currentTimeMillis() - 31536000000L),
                Country.United_States,
                "https://example.com/pic.jpg",
                roles
        );

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidUser_BlankFirstName() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        User user = new User(
                "",
                null,
                "Doe",
                "password123",
                "john.doe@example.com",
                new Date(System.currentTimeMillis() - 31536000000L),
                Country.United_States,
                "https://example.com/pic.jpg",
                roles
        );

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("First name is required")));
    }

    @Test
    void testInvalidUser_LowercaseFirstName() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        User user = new User(
                "john",
                null,
                "Doe",
                "password123",
                "john.doe@example.com",
                new Date(System.currentTimeMillis() - 31536000000L),
                Country.United_States,
                "https://example.com/pic.jpg",
                roles
        );

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("First name needs to start with capital")));
    }

    @Test
    void testInvalidUser_InvalidEmail() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        User user = new User(
                "John",
                null,
                "Doe",
                "password123",
                "invalid-email",
                new Date(System.currentTimeMillis() - 31536000000L),
                Country.United_States,
                "https://example.com/pic.jpg",
                roles
        );

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email should be valid")));
    }

    @Test
    void testInvalidUser_ShortPassword() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        User user = new User(
                "John",
                null,
                "Doe",
                "short",
                "john.doe@example.com",
                new Date(System.currentTimeMillis() - 31536000000L),
                Country.United_States,
                "https://example.com/pic.jpg",
                roles
        );

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password must be at least 6 characters")));
    }

    @Test
    void testInvalidUser_FutureDate() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        User user = new User(
                "John",
                null,
                "Doe",
                "password123",
                "john.doe@example.com",
                new Date(System.currentTimeMillis() + 31536000000L),
                Country.United_States,
                "https://example.com/pic.jpg",
                roles
        );

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("User must exist")));
    }

    @Test
    void testGettersAndSetters() {
        User user = new User();
        String firstName = "John";
        String middleName = "James";
        String lastName = "Doe";
        String password = "password123";
        String email = "john.doe@example.com";
        Date dateOfBirth = new Date(System.currentTimeMillis() - 31536000000L);
        Country location = Country.United_States;
        String profilePicture = "https://example.com/pic.jpg";
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);

        user.setId(1L);
        user.setFirstName(firstName);
        user.setMiddleName(middleName);
        user.setLastName(lastName);
        user.setPassword(password);
        user.setEmail(email);
        user.setDateOfBirth(dateOfBirth);
        user.setLocation(location);
        user.setProfilePicture(profilePicture);
        user.setRoles(roles);

        assertEquals(1L, user.getId());
        assertEquals(firstName, user.getFirstName());
        assertEquals(middleName, user.getMiddleName());
        assertEquals(lastName, user.getLastName());
        assertEquals(password, user.getPassword());
        assertEquals(email, user.getEmail());
        assertEquals(dateOfBirth, user.getDateOfBirth());
        assertEquals(location, user.getLocation());
        assertEquals(profilePicture, user.getProfilePicture());
        assertEquals(roles, user.getRoles());
    }
}
