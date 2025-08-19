package elte.icj06o.auction.service;

import elte.icj06o.auction.crypto.PaillierCryptoService;
import elte.icj06o.auction.model.*;
import elte.icj06o.auction.repository.AuctionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigInteger;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuctionServiceTest {
    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private PaillierCryptoService paillierCryptoService;

    @InjectMocks
    private AuctionServiceImpl auctionService;

    private User testUser;
    private Auction testAuction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        testUser = new User(
                "John",
                null,
                "Doe",
                "password123",
                "john.doe@example.com",
                new Date(System.currentTimeMillis() - 31536000000L),
                Country.United_States,
                "https://example.com/pic.jpg",
                roles
        );
        testUser.setId(1L);

        testAuction = new Auction(
                "Test Auction",
                "Description for the test auction",
                100,
                10,
                200,
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + 86400000), // +1 day
                false,
                testUser,
                null
        );
        testAuction.setId(1L);
    }

    @Test
    void testCreateAuction_ValidAuction() {
        Auction auction = new Auction(
                "Test Auction",
                "Description for the test auction",
                100,
                10,
                200,
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + 86400000),
                false,
                null,
                null
        );

        auctionService.createAuction(auction, testUser);

        verify(auctionRepository).save(auction);
        assertEquals(testUser, auction.getCreatedBy());
        assertNotNull(auction.getCreatedTime());
        assertNull(auction.getId());
    }

    @Test
    void testCreateAuction_InvalidDates() {
        Auction auction = new Auction(
                "Test Auction",
                "Description for the test auction",
                100,
                10,
                200,
                new Date(System.currentTimeMillis() + 86400000),
                new Date(System.currentTimeMillis()),
                false,
                null,
                null
        );

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> auctionService.createAuction(auction, testUser));

        assertEquals("Start date must be before end date.", exception.getMessage());
        verify(auctionRepository, never()).save(any(Auction.class));
    }

    @Test
    void testCreateAuction_PublicWithNullBidIncrement() {
        Auction auction = new Auction(
                "Test Auction",
                "Description for the test auction",
                100,
                null,
                200,
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + 86400000),
                false,
                null,
                null
        );

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> auctionService.createAuction(auction, testUser));

        assertEquals("Bid increment is required.", exception.getMessage());
        verify(auctionRepository, never()).save(any(Auction.class));
    }

    @Test
    void testGetAuctionById() {
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(testAuction));

        Optional<Auction> result = auctionService.getAuctionById(1L);

        assertTrue(result.isPresent());
        assertEquals(testAuction, result.get());
    }

    @Test
    void testProcessPublicAuction_WithBids() {
        User bidder1 = new User();
        bidder1.setId(2L);

        User bidder2 = new User();
        bidder2.setId(3L);

        Bid bid1 = new Bid();
        bid1.setAmount(110);
        bid1.setUser(bidder1);

        Bid bid2 = new Bid();
        bid2.setAmount(120);
        bid2.setUser(bidder2);

        List<Bid> bids = Arrays.asList(bid1, bid2);
        testAuction.setBids(bids);

        auctionService.processPublicAuction(testAuction);

        verify(auctionRepository).updateWinner(testAuction.getId(), bidder2);
    }

    @Test
    void testProcessSecretAuction_WithBids() {
        User bidder1 = new User();
        bidder1.setId(2L);

        User bidder2 = new User();
        bidder2.setId(3L);

        Bid bid1 = new Bid();
        bid1.setEncryptedAmount("110");
        bid1.setUser(bidder1);

        Bid bid2 = new Bid();
        bid2.setEncryptedAmount("120");
        bid2.setUser(bidder2);

        List<Bid> bids = Arrays.asList(bid1, bid2);
        testAuction.setBids(bids);
        testAuction.setIsSecret(true);

        when(paillierCryptoService.isGreater(new BigInteger("120"), new BigInteger("110"))).thenReturn(true);

        auctionService.processSecretAuction(testAuction);

        verify(auctionRepository).updateWinner(testAuction.getId(), bidder2); // bidder2 has the highest bid
    }

    @Test
    void testGetNextPrice_NoExistingBids() {
        testAuction.setBids(new ArrayList<>());

        Integer nextPrice = auctionService.getNextPrice(testAuction);

        assertEquals(testAuction.getStartingPrice(), nextPrice);
    }

    @Test
    void testGetNextPrice_WithExistingBids() {
        User bidder = new User();
        bidder.setId(2L);

        Bid bid = new Bid();
        bid.setAmount(150);
        bid.setUser(bidder);

        testAuction.setBids(Collections.singletonList(bid));
        testAuction.setBidIncrement(10);

        Integer nextPrice = auctionService.getNextPrice(testAuction);

        assertEquals(160, nextPrice);
    }
}

