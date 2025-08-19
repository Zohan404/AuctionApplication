package elte.icj06o.auction.service;

import elte.icj06o.auction.dto.ReviewList;
import elte.icj06o.auction.dto.UserBadge;
import elte.icj06o.auction.model.Auction;
import elte.icj06o.auction.model.Review;
import elte.icj06o.auction.model.ReviewKey;
import elte.icj06o.auction.model.User;
import elte.icj06o.auction.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {
    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewServiceImp reviewService;

    @Captor
    private ArgumentCaptor<Review> reviewCaptor;

    private User fromUser;
    private User toUser;
    private Review review;
    private Auction auction;
    private ReviewKey reviewKey;

    @BeforeEach
    void setUp() {
        fromUser = new User();
        fromUser.setId(1L);
        fromUser.setFirstName("John");
        fromUser.setMiddleName("M");
        fromUser.setLastName("Doe");
        fromUser.setProfilePicture("http://example.com/john.jpg");
        fromUser.setWinedAuctions(new ArrayList<>());

        toUser = new User();
        toUser.setId(2L);
        toUser.setFirstName("Jane");
        toUser.setLastName("Smith");

        auction = new Auction();
        auction.setId(1L);
        auction.setCreatedBy(toUser);

        fromUser.getWinedAuctions().add(auction);

        reviewKey = new ReviewKey(fromUser.getId(), toUser.getId());
        review = new Review();
        review.setId(reviewKey);
        review.setFromUser(fromUser);
        review.setToUser(toUser);
        review.setText("Great seller!");
        review.setRating(5);
        review.setCreatedTime(new Date());
    }

    @Test
    void getAllReviewsByReceivingUser_ShouldReturnReviews() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> mockPage = new PageImpl<>(List.of(review));
        when(reviewRepository.getAllById_ToUserId(toUser.getId(), pageable)).thenReturn(mockPage);

        Page<Review> result = reviewService.getAllReviewsByReceivingUser(toUser.getId(), pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(review, result.getContent().get(0));
    }

    @Test
    void createReview_ValidReview_ShouldSaveAndReturnReview() {
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(reviewRepository.findAll()).thenReturn(new ArrayList<>());

        Review result = reviewService.createReview(review);

        assertNotNull(result);
        assertEquals("Great seller!", result.getText());
        assertEquals(5, result.getRating());
        verify(reviewRepository).save(review);
    }

    @Test
    void createReview_SelfReview_ShouldThrowException() {
        review.setToUser(fromUser);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> reviewService.createReview(review));
        assertEquals("You cannot review yourself.", exception.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void createReview_NoWonAuctions_ShouldThrowException() {
        fromUser.setWinedAuctions(new ArrayList<>());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> reviewService.createReview(review));
        assertEquals("You have not won any auction from this user.", exception.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void createReview_AlreadyReviewed_ShouldThrowException() {
        when(reviewRepository.findAll()).thenReturn(Collections.singletonList(review));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> reviewService.createReview(review));
        assertEquals("You cannot review a user twice.", exception.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void updateReview_ExistingReview_ShouldUpdateAndReturn() {
        Review updatedReview = new Review();
        updatedReview.setText("Updated text");
        updatedReview.setRating(4);

        when(reviewRepository.findById(reviewKey)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Review result = reviewService.updateReview(fromUser.getId(), toUser.getId(), updatedReview);

        assertEquals("Updated text", result.getText());
        assertEquals(4, result.getRating());
        assertNotNull(result.getCreatedTime());
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void updateReview_NonExistingReview_ShouldThrowException() {
        Review updatedReview = new Review();
        when(reviewRepository.findById(reviewKey)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> reviewService.updateReview(fromUser.getId(), toUser.getId(), updatedReview));
        assertEquals("Review not found.", exception.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void deleteReview_ShouldCallRepositoryDelete() {
        reviewService.deleteReview(fromUser.getId(), toUser.getId());

        verify(reviewRepository).deleteById(reviewKey);
    }

    @Test
    void reviewToReviewList_ShouldConvertCorrectly() {
        ReviewList result = reviewService.reviewToReviewList(review);

        assertNotNull(result);
        assertEquals("Great seller!", result.getText());
        assertEquals(5, result.getRating());

        UserBadge userBadge = result.getUser();
        assertEquals(fromUser.getId(), userBadge.getId());
        assertEquals("John", userBadge.getFirstName());
        assertEquals("M", userBadge.getMiddleName());
        assertEquals("Doe", userBadge.getLastName());
        assertEquals("http://example.com/john.jpg", userBadge.getProfilePicture());
    }
}
