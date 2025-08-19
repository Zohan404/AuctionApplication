package elte.icj06o.auction.repository;

import elte.icj06o.auction.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class AuctionRepositoryTest {
    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BidRepository bidRepository;

    private User seller;
    private User buyer;
    private Auction activeAuction;
    private Auction closedAuction;

    @BeforeEach
    public void setup() {
        seller = new User(
                "Seller",
                null,
                "User",
                "password123",
                "seller@example.com",
                new Date(90, Calendar.FEBRUARY, 1),
                Country.United_States,
                "https://example.com/seller.jpg",
                Set.of(Role.USER)
        );
        userRepository.save(seller);

        buyer = new User(
                "Buyer",
                null,
                "User",
                "password456",
                "buyer@example.com",
                new Date(95, Calendar.JUNE, 15),
                Country.United_Kingdom,
                "https://example.com/buyer.jpg",
                Set.of(Role.USER)
        );
        userRepository.save(buyer);

        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);

        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterday = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        Date tomorrow = calendar.getTime();

        activeAuction = new Auction(
                "Active Auction",
                "This is an active auction",
                100,
                10,
                500,
                yesterday,
                tomorrow,
                false,
                seller,
                null
        );
        auctionRepository.save(activeAuction);

        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        Date twoDaysAgo = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        yesterday = calendar.getTime();

        closedAuction = new Auction(
                "Closed Auction",
                "This auction has ended",
                200,
                20,
                1000,
                twoDaysAgo,
                yesterday,
                false,
                seller,
                null
        );
        auctionRepository.save(closedAuction);

        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        tomorrow = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        Date threeDaysFromNow = calendar.getTime();

        Auction upcomingAuction = new Auction(
                "Upcoming Auction",
                "This auction will start soon",
                300,
                30,
                1500,
                tomorrow,
                threeDaysFromNow,
                false,
                seller,
                null
        );
        auctionRepository.save(upcomingAuction);

        Auction secretAuction = new Auction(
                "Secret Auction",
                "This is a secret auction",
                400,
                40,
                2000,
                yesterday,
                tomorrow,
                true,
                seller,
                null
        );
        auctionRepository.save(secretAuction);
    }

    @Test
    public void testGetAllByCreatedBy() {
        Page<Auction> sellerAuctions = auctionRepository.getAllByCreatedBy(
                seller, PageRequest.of(0, 10));

        assertEquals(4, sellerAuctions.getTotalElements());
        assertTrue(sellerAuctions.getContent().stream()
                .allMatch(auction -> auction.getCreatedBy().equals(seller)));
    }

    @Test
    public void testGetAllByCreatedByIn() {
        Set<User> users = new HashSet<>();
        users.add(seller);

        Page<Auction> auctions = auctionRepository.getAllByCreatedByIn(
                users, PageRequest.of(0, 10));

        assertEquals(4, auctions.getTotalElements());
    }

    @Test
    public void testFindFilteredAuctions_ActiveOnly() {
        Page<Auction> result = auctionRepository.findFilteredAuctions(
                1000,
                2000,
                "",
                false,
                true,
                false,
                true,
                false,
                PageRequest.of(0, 10)
        );

        assertEquals(1, result.getTotalElements());
        assertEquals("Active Auction", result.getContent().get(0).getTitle());
    }

    @Test
    public void testFindFilteredAuctions_ClosedOnly() {
        Page<Auction> result = auctionRepository.findFilteredAuctions(
                1000,
                2000,
                "",
                false,
                false,
                true,
                true,
                false,
                PageRequest.of(0, 10)
        );

        assertEquals(1, result.getTotalElements());
        assertEquals("Closed Auction", result.getContent().get(0).getTitle());
    }

    @Test
    public void testFindFilteredAuctions_UpcomingOnly() {
        Page<Auction> result = auctionRepository.findFilteredAuctions(
                1000,
                2000,
                "",
                true,
                false,
                false,
                true,
                false,
                PageRequest.of(0, 10)
        );

        assertEquals(1, result.getTotalElements());
        assertEquals("Upcoming Auction", result.getContent().get(0).getTitle());
    }

    @Test
    public void testFindFilteredAuctions_SecretOnly() {
        Page<Auction> result = auctionRepository.findFilteredAuctions(
                1000,
                2000,
                "",
                false,
                true,
                false,
                false,
                true,
                PageRequest.of(0, 10)
        );

        assertEquals(1, result.getTotalElements());
        assertEquals("Secret Auction", result.getContent().get(0).getTitle());
    }

    @Test
    public void testFindFilteredAuctions_TitleFilter() {
        Page<Auction> result = auctionRepository.findFilteredAuctions(
                1000,
                2000,
                "Upcoming",
                true,
                true,
                true,
                true,
                false,
                PageRequest.of(0, 10)
        );

        assertEquals(1, result.getTotalElements());
        assertEquals("Upcoming Auction", result.getContent().get(0).getTitle());
    }

    @Test
    public void testFindFilteredAuctions_PriceFilters() {
        Page<Auction> result = auctionRepository.findFilteredAuctions(
                250,
                2000,
                "",
                true,
                true,
                true,
                true,
                false,
                PageRequest.of(0, 10)
        );

        assertEquals(2, result.getTotalElements());
        assertFalse(result.getContent().stream()
                .anyMatch(auction -> auction.getTitle().equals("Upcoming Auction")));
    }

    @Test
    public void testFindClosedAuctions() {
        Date now = new Date();

        List<Auction> closedAuctions = auctionRepository.findClosedAuctions(now);

        assertEquals(1, closedAuctions.size());
        assertEquals("Closed Auction", closedAuctions.get(0).getTitle());
    }

    @Test
    public void testUpdateWinner() {
        auctionRepository.updateWinner(closedAuction.getId(), buyer);

        Optional<Auction> updatedAuction = auctionRepository.findById(closedAuction.getId());
        assertTrue(updatedAuction.isPresent());
        assertEquals(buyer.getId(), updatedAuction.get().getWinner().getId());
    }

    @Test
    public void testBidAffectsFilteredAuctionsQuery() {
        Bid bid = new Bid(activeAuction, buyer, 150, "encrypted", new Date());
        bidRepository.save(bid);

        Page<Auction> result = auctionRepository.findFilteredAuctions(
                160,
                2000,
                "",
                false,
                true,
                false,
                true,
                false,
                PageRequest.of(0, 10)
        );

        assertEquals(1, result.getTotalElements());

        Page<Auction> filteredOut = auctionRepository.findFilteredAuctions(
                159,
                2000,
                "",
                false,
                true,
                false,
                true,
                false,
                PageRequest.of(0, 10)
        );

        assertEquals(0, filteredOut.getTotalElements());
    }
}
