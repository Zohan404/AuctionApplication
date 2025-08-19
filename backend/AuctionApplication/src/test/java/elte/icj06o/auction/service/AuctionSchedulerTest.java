package elte.icj06o.auction.service;

import elte.icj06o.auction.model.Auction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuctionSchedulerTest {
    @Mock
    private AuctionService auctionService;

    @InjectMocks
    private AuctionScheduler auctionScheduler;

    private List<Auction> mockAuctions;
    private Auction publicAuction;
    private Auction secretAuction;

    @BeforeEach
    void setUp() {
        mockAuctions = new ArrayList<>();

        publicAuction = new Auction();
        publicAuction.setId(1L);
        publicAuction.setIsSecret(false);

        secretAuction = new Auction();
        secretAuction.setId(2L);
        secretAuction.setIsSecret(true);

        mockAuctions.add(publicAuction);
        mockAuctions.add(secretAuction);
    }

    @Test
    void checkAuctionToClose_ShouldProcessAllClosedAuctions() {
        when(auctionService.getClosedAuctions(any(Date.class))).thenReturn(mockAuctions);

        auctionScheduler.checkAuctionToClose();

        verify(auctionService, times(1)).processPublicAuction(publicAuction);
        verify(auctionService, times(1)).processSecretAuction(secretAuction);
    }

    @Test
    void checkAuctionToClose_ShouldContinueProcessingDespiteException() {
        when(auctionService.getClosedAuctions(any(Date.class))).thenReturn(mockAuctions);
        doThrow(new RuntimeException("No winner")).when(auctionService).processPublicAuction(publicAuction);

        auctionScheduler.checkAuctionToClose();

        verify(auctionService, times(1)).processPublicAuction(publicAuction);
        verify(auctionService, times(1)).processSecretAuction(secretAuction);
    }
}
