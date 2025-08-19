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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BidServiceTest {
    @Mock
    private BidRepository bidRepository;

    @Mock
    private PaillierCryptoService paillierCryptoService;

    @InjectMocks
    private BidServiceImpl bidService;

    private User user;
    private Auction publicAuction;
    private Auction secretAuction;
    private Bid bid;
    private Date currentDate;
    private Date futureDate;
    private Date pastDate;

    @BeforeEach
    void setUp() {
        currentDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        futureDate = calendar.getTime();

        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        pastDate = calendar.getTime();

        user = new User();
        user.setId(1L);

        publicAuction = new Auction();
        publicAuction.setId(1L);
        publicAuction.setStartingPrice(100);
        publicAuction.setBidIncrement(10);
        publicAuction.setBuyNowPrice(500);
        publicAuction.setIsSecret(false);
        publicAuction.setStartDate(pastDate);
        publicAuction.setEndDate(futureDate);
        publicAuction.setBids(new ArrayList<>());

        secretAuction = new Auction();
        secretAuction.setId(2L);
        secretAuction.setStartingPrice(100);
        secretAuction.setIsSecret(true);
        secretAuction.setStartDate(pastDate);
        secretAuction.setEndDate(futureDate);
        secretAuction.setBids(new ArrayList<>());

        bid = new Bid();
        bid.setId(new BidKey(publicAuction.getId(), user.getId()));
        bid.setAuction(publicAuction);
        bid.setUser(user);
        bid.setAmount(120);
        bid.setTime(currentDate);
    }

    @Test
    void getBidsByUser_ShouldReturnPageOfBids() {
        Pageable pageable = Pageable.unpaged();
        List<Bid> bidList = Collections.singletonList(bid);
        Page<Bid> bidPage = new PageImpl<>(bidList);

        when(bidRepository.getAllByUser(user, pageable)).thenReturn(bidPage);

        Page<Bid> result = bidService.getBidsByUser(user, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(bid, result.getContent().get(0));
        verify(bidRepository).getAllByUser(user, pageable);
    }

    @Test
    void handlePublicBid_ShouldCreateNewBid_WhenNoPreviousBidExists() {
        int bidAmount = 150;

        when(bidRepository.existsById(any(BidKey.class))).thenReturn(false);
        when(bidRepository.save(any(Bid.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Bid result = bidService.handlePublicBid(publicAuction, user, bidAmount);

        assertNotNull(result);
        assertEquals(bidAmount, result.getAmount());
        assertEquals(user, result.getUser());
        assertEquals(publicAuction, result.getAuction());
        verify(bidRepository).save(any(Bid.class));
    }

    @Test
    void handlePublicBid_ShouldUpdateExistingBid_WhenBidExists() {
        int initialBidAmount = 120;
        int newBidAmount = 200;

        Bid existingBid = new Bid();
        existingBid.setId(new BidKey(publicAuction.getId(), user.getId()));
        existingBid.setAuction(publicAuction);
        existingBid.setUser(user);
        existingBid.setAmount(initialBidAmount);
        existingBid.setTime(pastDate);

        publicAuction.getBids().add(existingBid);

        when(bidRepository.existsById(any(BidKey.class))).thenReturn(true);
        when(bidRepository.findById(any(BidKey.class))).thenReturn(Optional.of(existingBid));
        when(bidRepository.save(any(Bid.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Bid result = bidService.handlePublicBid(publicAuction, user, newBidAmount);

        assertNotNull(result);
        assertEquals(newBidAmount, result.getAmount());
        verify(bidRepository).save(any(Bid.class));
    }

    @Test
    void handlePublicBid_ShouldThrowException_WhenBidAmountLessThanStartingPrice() {
        int bidAmount = 50;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bidService.handlePublicBid(publicAuction, user, bidAmount)
        );

        assertEquals("Bid must be at least the starting price.", exception.getMessage());
    }

    @Test
    void handlePublicBid_ShouldThrowException_WhenAuctionNotStarted() {
        publicAuction.setStartDate(futureDate);
        int bidAmount = 150;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bidService.handlePublicBid(publicAuction, user, bidAmount)
        );

        assertEquals("Auction has not yet started.", exception.getMessage());
    }

    @Test
    void handlePublicBid_ShouldThrowException_WhenAuctionEnded() {
        publicAuction.setEndDate(pastDate);
        int bidAmount = 150;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bidService.handlePublicBid(publicAuction, user, bidAmount)
        );

        assertEquals("Auction has already ended.", exception.getMessage());
    }

    @Test
    void handlePublicBid_ShouldThrowException_WhenAuctionHasWinner() {
        User winner = new User();
        winner.setId(2L);
        publicAuction.setWinner(winner);
        int bidAmount = 150;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bidService.handlePublicBid(publicAuction, user, bidAmount)
        );

        assertEquals("Auction has already ended.", exception.getMessage());
    }

    @Test
    void handlePublicBid_ShouldThrowException_WhenBidNotHighEnough() {
        int existingBidAmount = 200;
        int bidAmount = 205;

        Bid existingBid = new Bid();
        existingBid.setAmount(existingBidAmount);
        publicAuction.getBids().add(existingBid);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bidService.handlePublicBid(publicAuction, user, bidAmount)
        );

        assertEquals("Bid must exceed the current highest bid: " + (existingBidAmount + publicAuction.getBidIncrement()) + ".", exception.getMessage());
    }

    @Test
    void handlePublicBid_ShouldThrowException_WhenTryingOnSecretAuction() {
        int bidAmount = 150;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bidService.handlePublicBid(secretAuction, user, bidAmount)
        );

        assertEquals("Not a public auction", exception.getMessage());
    }

    @Test
    void handleSecretBid_ShouldCreateNewBid_WhenNoPreviousBidExists() {
        int bidAmount = 150;
        String encryptedAmount = "encryptedValue";

        when(paillierCryptoService.encrypt(bidAmount)).thenReturn(encryptedAmount);
        when(bidRepository.existsById(any(BidKey.class))).thenReturn(false);
        when(bidRepository.save(any(Bid.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Bid result = bidService.handleSecretBid(secretAuction, user, bidAmount);

        assertNotNull(result);
        assertEquals(encryptedAmount, result.getEncryptedAmount());
        assertNull(result.getAmount());
        assertEquals(user, result.getUser());
        assertEquals(secretAuction, result.getAuction());
        verify(bidRepository).save(any(Bid.class));
    }

    @Test
    void handleSecretBid_ShouldUpdateExistingBid() {
        int bidAmount = 200;
        String encryptedAmount = "newEncryptedValue";

        Bid existingBid = new Bid();
        existingBid.setId(new BidKey(secretAuction.getId(), user.getId()));
        existingBid.setAuction(secretAuction);
        existingBid.setUser(user);
        existingBid.setEncryptedAmount("oldEncryptedValue");
        existingBid.setTime(pastDate);

        secretAuction.getBids().add(existingBid);

        when(paillierCryptoService.encrypt(bidAmount)).thenReturn(encryptedAmount);
        when(bidRepository.existsById(any(BidKey.class))).thenReturn(true);
        when(bidRepository.findById(any(BidKey.class))).thenReturn(Optional.of(existingBid));
        when(bidRepository.save(any(Bid.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Bid result = bidService.handleSecretBid(secretAuction, user, bidAmount);

        assertNotNull(result);
        assertEquals(encryptedAmount, result.getEncryptedAmount());
        verify(bidRepository).save(any(Bid.class));
    }

    @Test
    void handleSecretBid_ShouldThrowException_WhenBidAmountLessThanStartingPrice() {
        int bidAmount = 50;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bidService.handleSecretBid(secretAuction, user, bidAmount)
        );

        assertEquals("Bid must be at least the starting price.", exception.getMessage());
    }

    @Test
    void handleSecretBid_ShouldThrowException_WhenAuctionNotStarted() {
        secretAuction.setStartDate(futureDate);
        int bidAmount = 150;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bidService.handleSecretBid(secretAuction, user, bidAmount)
        );

        assertEquals("Auction has not yet started.", exception.getMessage());
    }

    @Test
    void handleSecretBid_ShouldThrowException_WhenAuctionEnded() {
        secretAuction.setEndDate(pastDate);
        int bidAmount = 150;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bidService.handleSecretBid(secretAuction, user, bidAmount)
        );

        assertEquals("Auction has already ended.", exception.getMessage());
    }

    @Test
    void handleSecretBid_ShouldThrowException_WhenAuctionHasWinner() {
        User winner = new User();
        winner.setId(2L);
        secretAuction.setWinner(winner);
        int bidAmount = 150;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bidService.handleSecretBid(secretAuction, user, bidAmount)
        );

        assertEquals("Auction has already ended.", exception.getMessage());
    }

    @Test
    void handleSecretBid_ShouldThrowException_WhenTryingOnPublicAuction() {
        int bidAmount = 150;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bidService.handleSecretBid(publicAuction, user, bidAmount)
        );

        assertEquals("Not a secret auction.", exception.getMessage());
    }

    @Test
    void bidToBidList_ShouldConvertBidToDto() {
        UserBadge userBadge = new UserBadge(1L, "John", null, "Doe", null);
        AuctionBadge auctionBadge = new AuctionBadge(1L, "Test Auction", null, null);

        BidList result = bidService.bidToBidList(bid, userBadge, auctionBadge);

        assertNotNull(result);
        assertEquals(userBadge, result.getUser());
        assertEquals(auctionBadge, result.getAuction());
        assertEquals(bid.getAmount(), result.getAmount());
        assertEquals(bid.getTime(), result.getTime());
    }

    @Test
    void bidToBidList_ShouldHideAmountForSecretAuction() {
        UserBadge userBadge = new UserBadge(1L, "John", null, "Doe", null);
        AuctionBadge auctionBadge = new AuctionBadge(2L, "Secret Auction", null, null);

        Bid secretBid = new Bid();
        secretBid.setId(new BidKey(secretAuction.getId(), user.getId()));
        secretBid.setAuction(secretAuction);
        secretBid.setUser(user);
        secretBid.setAmount(120); // Should not be exposed
        secretBid.setEncryptedAmount("encryptedValue");
        secretBid.setTime(currentDate);

        BidList result = bidService.bidToBidList(secretBid, userBadge, auctionBadge);

        assertNotNull(result);
        assertNull(result.getAmount());
    }
}
