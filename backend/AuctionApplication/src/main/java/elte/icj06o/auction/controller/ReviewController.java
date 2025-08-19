package elte.icj06o.auction.controller;

import elte.icj06o.auction.dto.ReviewList;
import elte.icj06o.auction.model.Review;
import elte.icj06o.auction.model.User;
import elte.icj06o.auction.service.ReviewService;
import elte.icj06o.auction.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;

    public ReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @GetMapping("/user/{id}")
    public List<ReviewList> getReviewsByReceivingUser(@PathVariable Long id,
                                                      @RequestParam(defaultValue = "0") int page) {
        Sort.Direction direction = Sort.Direction.fromString("desc");
        Sort sortBy = Sort.by(direction, "createdTime");
        Pageable pageable = PageRequest.of(page, 10, sortBy);

        return reviewService.getAllReviewsByReceivingUser(id, pageable)
                .stream()
                .map(reviewService::reviewToReviewList)
                .toList();
    }

    @PostMapping("/to/{id}")
    public ResponseEntity<?> createReview(@PathVariable Long id,
                                          @AuthenticationPrincipal UserDetails userDetails,
                                          @Valid @RequestBody Review review) throws RuntimeException {
        Optional<User> fromUser = userService.getUserByEmail(userDetails.getUsername());
        Optional<User> toUser = userService.getUserById(id);

        if (fromUser.isPresent() && toUser.isPresent()) {
            return ResponseEntity.ok(reviewService.reviewToReviewList(reviewService.createReview(
                    new Review(fromUser.get(), toUser.get(), review.getText(), review.getRating()))));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/to/{id}")
    public ResponseEntity<ReviewList> updateReview(@PathVariable Long id,
                                                   @AuthenticationPrincipal UserDetails userDetails,
                                                   @Valid @RequestBody Review review) {
        Optional<User> loggedInUser = userService.getUserByEmail(userDetails.getUsername());

        try {
            return loggedInUser
                    .map(user -> ResponseEntity.ok(reviewService.reviewToReviewList(
                            reviewService.updateReview(user.getId(), id, review))))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("to/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> loggedInUser = userService.getUserByEmail(userDetails.getUsername());
        if (loggedInUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        reviewService.deleteReview(loggedInUser.get().getId(), id);
        return ResponseEntity.noContent().build();
    }
}
