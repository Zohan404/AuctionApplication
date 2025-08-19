package elte.icj06o.auction.service;

import elte.icj06o.auction.crypto.PaillierCryptoService;
import elte.icj06o.auction.dto.*;
import elte.icj06o.auction.model.Auction;
import elte.icj06o.auction.model.Bid;
import elte.icj06o.auction.model.Picture;
import elte.icj06o.auction.model.User;
import elte.icj06o.auction.repository.AuctionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;

@Service
public class AuctionServiceImpl implements AuctionService {
    private final AuctionRepository auctionRepository;
    private final PaillierCryptoService paillierCryptoService;

    public AuctionServiceImpl(AuctionRepository auctionRepository,
                              PaillierCryptoService paillierCryptoService) {
        this.auctionRepository = auctionRepository;
        this.paillierCryptoService = paillierCryptoService;
    }

    public Page<Auction> getAllFilteredAuctions(
            Integer maxNextPrice,
            Integer maxBuyNowPrice,
            String title,
            Boolean includeUpcoming,
            Boolean includeActive,
            Boolean includeClosed,
            Boolean isPublic,
            Boolean isSecret,
            Pageable pageable) {
        return auctionRepository.findFilteredAuctions(
                maxNextPrice,
                maxBuyNowPrice,
                title,
                includeUpcoming,
                includeActive,
                includeClosed,
                isPublic,
                isSecret,
                pageable);
    }

    public Page<Auction> getAuctionsByUser(User user, Pageable pageable) {
        return auctionRepository.getAllByCreatedBy(user, pageable);
    }

    public Page<Auction> getAuctionsByUserFollowers(User user, Pageable pageable) {
        return auctionRepository.getAllByCreatedByIn(user.getFollowings(), pageable);
    }

    public Optional<Auction> getAuctionById(Long id) {
        return auctionRepository.findById(id);
    }

    public void createAuction(Auction auction, User user) {
        if (auction.getStartDate().after(auction.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date.");
        }

        if (!auction.getIsSecret() && auction.getBidIncrement() == null) {
            throw new IllegalArgumentException("Bid increment is required.");
        }

        if (!auction.getIsSecret() && auction.getBuyNowPrice() == null) {
            throw new IllegalArgumentException("Buy now price is required.");
        }

        if (!auction.getIsSecret() && (auction.getBuyNowPrice() - auction.getStartingPrice()) % auction.getBidIncrement() != 0) {
            throw new IllegalArgumentException("Bid increment has to be divided by the difference of buy now price and starting price.");
        }

        auction.setId(null);
        auction.setCreatedBy(user);
        auction.setCreatedTime(new Date());
        auctionRepository.save(auction);
    }

    public void processPublicAuction(Auction auction) {
        if (auction.getIsSecret()) return;

        List<Bid> bids = auction.getBids();
        Optional<Integer> optionalMaxAmount = bids.stream().map(Bid::getAmount).max(Integer::compareTo);
        if (optionalMaxAmount.isPresent()) {
            int maxAmount = optionalMaxAmount.get();
            Optional<Bid> winningBid = bids.stream().filter(bid -> bid.getAmount().equals(maxAmount)).findFirst();
            winningBid.ifPresent(bid -> auctionRepository.updateWinner(auction.getId(), bid.getUser()));
        }
    }

    public void processSecretAuction(Auction auction) {
        List<Bid> bids = auction.getBids();
        if (!auction.getIsSecret() && bids.isEmpty()) return;

        Bid winningBid = bids.get(0);
        BigInteger maxCipher = new BigInteger(winningBid.getEncryptedAmount());

        for (Bid bid : bids) {
            BigInteger currentCipher = new BigInteger(bid.getEncryptedAmount());

            if (paillierCryptoService.isGreater(currentCipher, maxCipher)) {
                maxCipher = currentCipher;
                winningBid = bid;
            }
        }

        auctionRepository.updateWinner(auction.getId(), winningBid.getUser());
    }

    public List<Auction> getClosedAuctions(Date date) {
        return auctionRepository.findClosedAuctions(date);
    }

    public AuctionList auctionToAuctionList(Auction auction, UserBadge createdBy, UserBadge winner) {
        return new AuctionList(
                auction.getId(),
                auction.getTitle(),
                auction.getDescription(),
                getNextPrice(auction),
                auction.getStartDate(),
                auction.getEndDate(),
                auction.getIsSecret(),
                createdBy,
                winner,
                auction.getPictures().stream()
                        .map(Picture::getPictureUrl)
                        .findFirst().orElse(null),
                auction.getCreatedTime()
        );
    }

    public InspectAuction auctionToInspectAuction(Auction auction, UserBadge createdBy, UserBadge winner, List<BidList> bids) {
        return new InspectAuction(
                auction.getId(),
                auction.getTitle(),
                auction.getDescription(),
                getNextPrice(auction),
                auction.getBidIncrement(),
                auction.getBuyNowPrice(),
                auction.getStartDate(),
                auction.getEndDate(),
                auction.getIsSecret(),
                createdBy,
                winner,
                auction.getPictures().stream()
                        .map(Picture::getPictureUrl)
                        .toList(),
                bids
        );
    }

    public AuctionBadge auctionToAuctionBadge(Auction auction) {
        return new AuctionBadge(
                auction.getId(),
                auction.getTitle(),
                auction.getPictures().stream()
                        .map(Picture::getPictureUrl)
                        .findFirst().orElse(null),
                auction.getWinner() != null ? auction.getWinner().getId() : null
        );
    }

    public Integer getNextPrice(Auction auction) {
        if (auction.getIsSecret() || auction.getBids().isEmpty()) {
            return auction.getStartingPrice();
        } else {
            List<Bid> bids = auction.getBids();
            int biggest = bids.get(0).getAmount();
            for (Bid bid : bids) {
                int currentAmount = bid.getAmount();
                if (currentAmount > biggest) {
                    biggest = currentAmount;
                }
            }
            return biggest + auction.getBidIncrement();
        }
    }
}
