package elte.icj06o.auction.repository;

import elte.icj06o.auction.model.Auction;
import elte.icj06o.auction.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    Page<Auction> getAllByCreatedBy(User createdBy, Pageable pageable);
    Page<Auction> getAllByCreatedByIn(Set<User> followings, Pageable pageable);
    @Query(
            value = """
        SELECT a FROM Auction a
        LEFT JOIN a.bids b
        WHERE
            (a.buyNowPrice <= :maxBuyNowPrice OR a.isSecret = TRUE)
            AND LOWER(a.title) LIKE LOWER(CONCAT('%', :title, '%'))
            AND (
                (:includeUpcoming = TRUE AND a.startDate > CURRENT_TIMESTAMP)
                OR (:includeActive = TRUE AND a.startDate <= CURRENT_TIMESTAMP AND a.endDate >= CURRENT_TIMESTAMP AND a.winner IS NULL)
                OR (:includeClosed = TRUE AND (a.endDate < CURRENT_TIMESTAMP OR a.winner IS NOT NULL))
            )
            AND (a.isSecret = :isSecret OR NOT a.isSecret = :isPublic)
        GROUP BY a.id
        HAVING COALESCE(MAX(b.amount) + a.bidIncrement, a.startingPrice) <= :maxNextPrice
        """,
            countQuery = """
        SELECT COUNT(a) FROM Auction a
        WHERE
            (a.buyNowPrice <= :maxBuyNowPrice OR a.isSecret = TRUE)
            AND LOWER(a.title) LIKE LOWER(CONCAT('%', :title, '%'))
            AND (
                (:includeUpcoming = TRUE AND a.startDate > CURRENT_TIMESTAMP)
                OR (:includeActive = TRUE AND a.startDate <= CURRENT_TIMESTAMP AND a.endDate >= CURRENT_TIMESTAMP AND a.winner IS NULL)
                OR (:includeClosed = TRUE AND (a.endDate < CURRENT_TIMESTAMP OR a.winner IS NOT NULL))
            )
            AND (a.isSecret = :isSecret OR NOT a.isSecret = :isPublic)
""")
    Page<Auction> findFilteredAuctions(
            Integer maxNextPrice,
            Integer maxBuyNowPrice,
            String title,
            boolean includeUpcoming,
            boolean includeActive,
            boolean includeClosed,
            boolean isPublic,
            boolean isSecret,
            Pageable pageable
    );
    @Query("""
        SELECT a FROM Auction a
        LEFT JOIN a.bids b
        WHERE
            a.endDate <= :date
            AND a.winner IS NULL 
        GROUP BY a.id
        HAVING COUNT(b.id) >= 0
""")
    List<Auction> findClosedAuctions(Date date);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Auction a SET a.winner = :winner WHERE a.id = :auctionId")
    void updateWinner(Long auctionId, User winner);
}
