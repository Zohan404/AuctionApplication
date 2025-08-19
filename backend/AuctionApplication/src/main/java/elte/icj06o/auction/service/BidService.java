package elte.icj06o.auction.service;

import elte.icj06o.auction.dto.AuctionBadge;
import elte.icj06o.auction.dto.BidList;
import elte.icj06o.auction.dto.UserBadge;
import elte.icj06o.auction.model.Auction;
import elte.icj06o.auction.model.Bid;
import elte.icj06o.auction.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BidService {
    Page<Bid> getBidsByUser(User user, Pageable pageable);
    Bid handlePublicBid(Auction auction, User user, Integer amount);
    Bid handleSecretBid(Auction auction, User user, Integer amount);
    BidList bidToBidList(Bid bid, UserBadge user, AuctionBadge auction);
}
