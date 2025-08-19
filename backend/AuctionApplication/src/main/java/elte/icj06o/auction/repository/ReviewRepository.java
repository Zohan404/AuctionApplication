package elte.icj06o.auction.repository;

import elte.icj06o.auction.model.Review;
import elte.icj06o.auction.model.ReviewKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, ReviewKey> {
    Page<Review> getAllById_ToUserId(Long toUserId, Pageable pageable);
}
