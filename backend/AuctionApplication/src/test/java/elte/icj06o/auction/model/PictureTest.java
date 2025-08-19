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

public class PictureTest {
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void testValidPicture() {
        User user = createValidUser();
        Auction auction = createValidAuction(user);
        Picture picture = new Picture(auction, "https://example.com/image.jpg");

        Set<ConstraintViolation<Picture>> violations = validator.validate(picture);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidPicture_BlankUrl() {
        User user = createValidUser();
        Auction auction = createValidAuction(user);
        Picture picture = new Picture(auction, "");

        Set<ConstraintViolation<Picture>> violations = validator.validate(picture);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Picture URL is required")));
    }

    @Test
    void testInvalidPicture_InvalidUrl() {
        User user = createValidUser();
        Auction auction = createValidAuction(user);
        Picture picture = new Picture(auction, "not-a-url");

        Set<ConstraintViolation<Picture>> violations = validator.validate(picture);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("A picture must be uploaded")));
    }

    @Test
    void testInvalidPicture_NotImageUrl() {
        User user = createValidUser();
        Auction auction = createValidAuction(user);
        Picture picture = new Picture(auction, "https://example.com/notimage.txt");

        Set<ConstraintViolation<Picture>> violations = validator.validate(picture);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("A picture must be uploaded")));
    }

    @Test
    void testGettersAndSetters() {
        User user = createValidUser();
        Auction auction = createValidAuction(user);
        Picture picture = new Picture();
        String pictureUrl = "https://example.com/image.jpg";

        picture.setId(1L);
        picture.setAuction(auction);
        picture.setPictureUrl(pictureUrl);

        assertEquals(1L, picture.getId());
        assertEquals(auction, picture.getAuction());
        assertEquals(pictureUrl, picture.getPictureUrl());
    }

    private User createValidUser() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        return new User(
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
    }

    private Auction createValidAuction(User user) {
        return new Auction(
                "Test Auction",
                "Description for the test auction",
                100,
                10,
                200,
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + 86400000),
                false,
                user,
                null
        );
    }
}
