package elte.icj06o.auction.service;

import elte.icj06o.auction.dto.*;
import elte.icj06o.auction.model.Auction;
import elte.icj06o.auction.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AuctionService {
    Page<Auction> getAllFilteredAuctions(Integer maxNextPrice, Integer maxBuyNowPrice, String title, Boolean includeUpcoming, Boolean includeActive, Boolean includeClosed, Boolean isPublic, Boolean isSecret, Pageable pageable);
    Page<Auction> getAuctionsByUser(User user, Pageable pageable);
    Page<Auction> getAuctionsByUserFollowers(User user, Pageable pageable);
    Optional<Auction> getAuctionById(Long id);
    void createAuction(Auction auction, User user);
    List<Auction> getClosedAuctions(Date date);
    void processSecretAuction(Auction auction);
    void processPublicAuction(Auction auction);
    AuctionList auctionToAuctionList(Auction auction, UserBadge createdBy, UserBadge winner);
    InspectAuction auctionToInspectAuction(Auction auction, UserBadge createdBy, UserBadge winner, List<BidList> bids);
    AuctionBadge auctionToAuctionBadge(Auction auction);
}
