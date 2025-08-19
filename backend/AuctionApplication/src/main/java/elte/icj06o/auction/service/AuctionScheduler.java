package elte.icj06o.auction.service;

import elte.icj06o.auction.model.Auction;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class AuctionScheduler {
    private final AuctionService auctionService;

    public AuctionScheduler(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void checkAuctionToClose() {
        List<Auction> closedAuctions = auctionService.getClosedAuctions(new Date());

        for (Auction auction : closedAuctions) {
            try {
                if (auction.getIsSecret()) {
                    auctionService.processSecretAuction(auction);
                } else {
                    auctionService.processPublicAuction(auction);
                }
            } catch (Exception e) {
                System.err.println("Auction does not have a winner");
            }
        }
    }
}
