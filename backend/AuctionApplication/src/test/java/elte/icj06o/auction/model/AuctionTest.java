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

public class AuctionTest {
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void testValidAuction() {
        User user = createValidUser();
        Auction auction = new Auction(
                "Test Auction",
                "Description for the test auction",
                100,
                10,
                200,
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + 86400000), // +1 day
                false,
                user,
                null
        );

        Set<ConstraintViolation<Auction>> violations = validator.validate(auction);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidAuction_BlankTitle() {
        User user = createValidUser();
        Auction auction = new Auction(
                "",
                "Description for the test auction",
                100,
                10,
                200,
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + 86400000), // +1 day
                false,
                user,
                null
        );

        Set<ConstraintViolation<Auction>> violations = validator.validate(auction);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Title is required")));
    }

    @Test
    void testInvalidAuction_NegativeStartingPrice() {
        User user = createValidUser();
        Auction auction = new Auction(
                "Test Auction",
                "Description for the test auction",
                -100,
                10,
                200,
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + 86400000),
                false,
                user,
                null
        );

        Set<ConstraintViolation<Auction>> violations = validator.validate(auction);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Starting price has to be positive")));
    }

    @Test
    void testInvalidAuction_NullStartDate() {
        User user = createValidUser();
        Auction auction = new Auction(
                "Test Auction",
                "Description for the test auction",
                100,
                10,
                200,
                null,
                new Date(System.currentTimeMillis() + 86400000),
                false,
                user,
                null
        );

        Set<ConstraintViolation<Auction>> violations = validator.validate(auction);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("start date")));
    }

    @Test
    void testInvalidAuction_NullEndDate() {
        User user = createValidUser();
        Auction auction = new Auction(
                "Test Auction",
                "Description for the test auction",
                100,
                10,
                200,
                new Date(System.currentTimeMillis()),
                null,
                false,
                user,
                null
        );

        Set<ConstraintViolation<Auction>> violations = validator.validate(auction);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("end date")));
    }

    @Test
    void testGettersAndSetters() {
        User user = createValidUser();
        Auction auction = new Auction();
        String title = "Test Auction";
        String description = "Description for the test auction";
        Integer startingPrice = 100;
        Integer bidIncrement = 10;
        Integer buyNowPrice = 200;
        Date startDate = new Date(System.currentTimeMillis());
        Date endDate = new Date(System.currentTimeMillis() + 86400000); // +1 day
        Boolean isSecret = false;

        auction.setId(1L);
        auction.setTitle(title);
        auction.setDescription(description);
        auction.setStartingPrice(startingPrice);
        auction.setBidIncrement(bidIncrement);
        auction.setBuyNowPrice(buyNowPrice);
        auction.setStartDate(startDate);
        auction.setEndDate(endDate);
        auction.setIsSecret(isSecret);
        auction.setCreatedBy(user);
        auction.setWinner(null);
        Date createdTime = new Date();
        auction.setCreatedTime(createdTime);

        assertEquals(1L, auction.getId());
        assertEquals(title, auction.getTitle());
        assertEquals(description, auction.getDescription());
        assertEquals(startingPrice, auction.getStartingPrice());
        assertEquals(bidIncrement, auction.getBidIncrement());
        assertEquals(buyNowPrice, auction.getBuyNowPrice());
        assertEquals(startDate, auction.getStartDate());
        assertEquals(endDate, auction.getEndDate());
        assertEquals(isSecret, auction.getIsSecret());
        assertEquals(user, auction.getCreatedBy());
        assertNull(auction.getWinner());
        assertEquals(createdTime, auction.getCreatedTime());
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
}
