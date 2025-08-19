package elte.icj06o.auction.repository;

import elte.icj06o.auction.model.Bid;
import elte.icj06o.auction.model.BidKey;
import elte.icj06o.auction.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, BidKey> {
    Page<Bid> getAllByUser(User user, Pageable pageable);
}
