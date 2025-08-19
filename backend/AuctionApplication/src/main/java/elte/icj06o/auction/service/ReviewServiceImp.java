package elte.icj06o.auction.service;

import elte.icj06o.auction.dto.ReviewList;
import elte.icj06o.auction.dto.UserBadge;
import elte.icj06o.auction.model.Review;
import elte.icj06o.auction.model.ReviewKey;
import elte.icj06o.auction.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class ReviewServiceImp implements ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewServiceImp(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Page<Review> getAllReviewsByReceivingUser(Long toUserId, Pageable pageable) {
        return reviewRepository.getAllById_ToUserId(toUserId, pageable);
    }

    public Review createReview(Review review) {
        if (review.getFromUser().getId().equals(review.getToUser().getId())) {
            throw new RuntimeException("You cannot review yourself.");
        }
        if (review.getFromUser().getWinedAuctions()
                .stream()
                .noneMatch(auction -> auction.getCreatedBy() == review.getToUser())) {
            throw new RuntimeException("You have not won any auction from this user.");
        }
        if (reviewRepository.findAll()
                .stream().
                anyMatch(existingReview -> review.getId().equals(existingReview.getId()))) {
            throw new RuntimeException("You cannot review a user twice.");
        }

        return reviewRepository.save(review);
    }

    public Review updateReview(Long fromUserId, Long toUserId, Review updatedReview) {
        Optional<Review> optionalReview = reviewRepository.findById(new ReviewKey(fromUserId, toUserId));
        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            review.setText(updatedReview.getText());
            review.setRating(updatedReview.getRating());
            review.setCreatedTime(new Date());
            return reviewRepository.save(review);
        } else {
            throw new RuntimeException("Review not found.");
        }
    }

    public void deleteReview(Long fromUserid, Long toUserId) {
        reviewRepository.deleteById(new ReviewKey(fromUserid, toUserId));
    }

    public ReviewList reviewToReviewList(Review review) {
        return new ReviewList(
                new UserBadge(
                        review.getFromUser().getId(),
                        review.getFromUser().getFirstName(),
                        review.getFromUser().getMiddleName(),
                        review.getFromUser().getLastName(),
                        review.getFromUser().getProfilePicture()
                ),
                review.getText(),
                review.getRating()
        );
    }
}
