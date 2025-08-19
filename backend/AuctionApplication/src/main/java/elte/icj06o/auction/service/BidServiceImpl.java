package elte.icj06o.auction.service;

import elte.icj06o.auction.crypto.PaillierCryptoService;
import elte.icj06o.auction.dto.AuctionBadge;
import elte.icj06o.auction.dto.BidList;
import elte.icj06o.auction.dto.UserBadge;
import elte.icj06o.auction.model.Auction;
import elte.icj06o.auction.model.Bid;
import elte.icj06o.auction.model.BidKey;
import elte.icj06o.auction.model.User;
import elte.icj06o.auction.repository.BidRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class BidServiceImpl implements BidService {
    private final BidRepository bidRepository;
    private final PaillierCryptoService paillierCryptoService;

    public BidServiceImpl(BidRepository bidRepository,
                          PaillierCryptoService paillierCryptoService) {
        this.bidRepository = bidRepository;
        this.paillierCryptoService = paillierCryptoService;
    }

    public Page<Bid> getBidsByUser(User user, Pageable pageable) {
        return bidRepository.getAllByUser(user, pageable);
    }

    public Bid handlePublicBid(Auction auction, User user, Integer amount) {
        if (auction.getIsSecret()) throw new IllegalArgumentException("Not a public auction");

        if (auction.getStartDate().after(new Date())) {
            throw new IllegalArgumentException("Auction has not yet started.");
        }

        if (auction.getEndDate().before(new Date()) || auction.getWinner() != null) {
            throw new IllegalArgumentException("Auction has already ended.");
        }

        if (amount < auction.getStartingPrice()) {
            throw new IllegalArgumentException("Bid must be at least the starting price.");
        }

        Integer highestBid = auction.getBids().stream()
                .map(Bid::getAmount)
                .max(Integer::compareTo)
                .orElse(0);

        if (amount < highestBid + auction.getBidIncrement()) {
            throw new IllegalArgumentException("Bid must exceed the current highest bid: "
                    + (highestBid + auction.getBidIncrement()) + ".");
        }

        if (bidRepository.existsById(new BidKey(auction.getId(), user.getId()))) {
            return updatePublicBid(auction, user, amount);
        } else {
            return createPublicBid(auction, user, amount);
        }
    }

    public Bid handleSecretBid(Auction auction, User user, Integer amount) {
        if (!auction.getIsSecret()) {
            throw new IllegalArgumentException("Not a secret auction.");
        }

        if (auction.getStartDate().after(new Date())) {
            throw new IllegalArgumentException("Auction has not yet started.");
        }

        if (auction.getEndDate().before(new Date()) || auction.getWinner() != null) {
            throw new IllegalArgumentException("Auction has already ended.");
        }

        if (amount < auction.getStartingPrice()) {
            throw new IllegalArgumentException("Bid must be at least the starting price.");
        }

        String encryptedAmount = paillierCryptoService.encrypt(amount);

        BidKey bidKey = new BidKey(auction.getId(), user.getId());
        if (bidRepository.existsById(bidKey)) {
            return updateSecretBid(auction, user, encryptedAmount);
        } else {
            return createSecretBid(auction, user, encryptedAmount);
        }
    }

    public BidList bidToBidList(Bid bid, UserBadge user, AuctionBadge auction) {
        Integer displayAmount = bid.getAuction().getIsSecret() ? null : bid.getAmount();

        return new BidList(
                user,
                auction,
                displayAmount,
                bid.getTime()
        );
    }

    private Bid createPublicBid(Auction auction, User user, Integer amount) {
        Date date = new Date();
        Bid bid = new Bid(auction, user, amount, null, date);
        auction.getBids().add(bid);
        return bidRepository.save(bid);
    }

    private Bid updatePublicBid(Auction auction, User user, Integer amount) {
        Bid existingBid = bidRepository.findById(new BidKey(auction.getId(), user.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Bid not found"));
        auction.getBids().remove(existingBid);

        if (existingBid.getAmount() >= amount) {
            throw new IllegalArgumentException("New bid must be higher than the previous one.");
        }
        existingBid.setAmount(amount);
        existingBid.setTime(new Date());
        auction.getBids().add(existingBid);
        return bidRepository.save(existingBid);
    }

    private Bid createSecretBid(Auction auction, User user, String encryptedAmount) {
        Date date = new Date();
        Bid bid = new Bid(auction, user, null, encryptedAmount, date);
        auction.getBids().add(bid);
        return bidRepository.save(bid);
    }

    private Bid updateSecretBid(Auction auction, User user, String encryptedAmount) {
        Bid existingBid = bidRepository.findById(new BidKey(auction.getId(), user.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Bid not found"));
        auction.getBids().remove(existingBid);

        existingBid.setEncryptedAmount(encryptedAmount);
        existingBid.setTime(new Date());
        auction.getBids().add(existingBid);
        return bidRepository.save(existingBid);
    }
}
