package elte.icj06o.auction.repository;

import elte.icj06o.auction.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class ReviewRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    private User user1;
    private User user2;

    @BeforeEach
    public void setup() {
        user1 = new User(
                "User",
                "One",
                "Test",
                "password123",
                "user1@example.com",
                new Date(90, Calendar.FEBRUARY, 1),
                Country.United_States,
                "https://example.com/user1.jpg",
                Set.of(Role.USER)
        );
        userRepository.save(user1);

        user2 = new User(
                "User",
                "Two",
                "Test",
                "password456",
                "user2@example.com",
                new Date(95, Calendar.JUNE, 15),
                Country.United_Kingdom,
                "https://example.com/user2.jpg",
                Set.of(Role.USER)
        );
        userRepository.save(user2);

        User user3 = new User(
                "User",
                "Three",
                "Test",
                "password789",
                "user3@example.com",
                new Date(85, Calendar.NOVEMBER, 20),
                Country.Canada,
                "https://example.com/user3.jpg",
                Set.of(Role.USER)
        );
        userRepository.save(user3);

        Review review1 = new Review(user1, user2, "Great experience with User Two", 5);
        reviewRepository.save(review1);

        Review review2 = new Review(user3, user2, "Good seller, fast shipping", 4);
        reviewRepository.save(review2);

        Review review3 = new Review(user2, user1, "Not bad, could have been better", 3);
        reviewRepository.save(review3);
    }

    @Test
    public void testGetAllById_ToUserId() {
        Page<Review> reviewsForUser2 = reviewRepository.getAllById_ToUserId(
                user2.getId(), PageRequest.of(0, 10));

        assertEquals(2, reviewsForUser2.getTotalElements());
        assertTrue(reviewsForUser2.getContent().stream()
                .allMatch(review -> review.getToUser().getId().equals(user2.getId())));

        Page<Review> reviewsForUser1 = reviewRepository.getAllById_ToUserId(
                user1.getId(), PageRequest.of(0, 10));

        assertEquals(1, reviewsForUser1.getTotalElements());
        assertEquals(user2.getId(), reviewsForUser1.getContent().get(0).getFromUser().getId());
    }

    @Test
    public void testSaveAndFindById() {
        ReviewKey reviewKey = new ReviewKey(user2.getId(), user1.getId());

        Optional<Review> foundReview = reviewRepository.findById(reviewKey);

        assertTrue(foundReview.isPresent());
        assertEquals("Not bad, could have been better", foundReview.get().getText());
        assertEquals(Integer.valueOf(3), foundReview.get().getRating());
    }

    @Test
    public void testReviewUpdate() {
        ReviewKey reviewKey = new ReviewKey(user1.getId(), user2.getId());
        Optional<Review> reviewToUpdate = reviewRepository.findById(reviewKey);
        assertTrue(reviewToUpdate.isPresent());

        Review review = reviewToUpdate.get();
        review.setText("Updated review text");
        review.setRating(4);
        reviewRepository.save(review);

        Optional<Review> updatedReview = reviewRepository.findById(reviewKey);
        assertTrue(updatedReview.isPresent());
        assertEquals("Updated review text", updatedReview.get().getText());
        assertEquals(Integer.valueOf(4), updatedReview.get().getRating());
    }

    @Test
    public void testDeleteReview() {
        ReviewKey reviewKey = new ReviewKey(user1.getId(), user2.getId());

        reviewRepository.deleteById(reviewKey);

        Optional<Review> deletedReview = reviewRepository.findById(reviewKey);
        assertFalse(deletedReview.isPresent());
    }
}
