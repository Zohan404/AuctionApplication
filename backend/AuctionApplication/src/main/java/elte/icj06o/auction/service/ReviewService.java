package elte.icj06o.auction.service;

import elte.icj06o.auction.dto.ReviewList;
import elte.icj06o.auction.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    Page<Review> getAllReviewsByReceivingUser(Long toUserId, Pageable pageable);
    Review createReview(Review review);
    Review updateReview(Long fromUserId, Long toUserId, Review updatedReview);
    void deleteReview(Long fromUserId, Long toUserId);
    ReviewList reviewToReviewList(Review review);
}
