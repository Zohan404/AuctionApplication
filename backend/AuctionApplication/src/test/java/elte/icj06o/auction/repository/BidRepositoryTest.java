package elte.icj06o.auction.repository;

import elte.icj06o.auction.AuctionApplication;
import elte.icj06o.auction.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class BidRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private BidRepository bidRepository;

    private User bidder1;
    private User bidder2;
    private Auction auction1;

    @BeforeEach
    public void setup() {
        User seller = new User(
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

        bidder1 = new User(
                "Bidder",
                "One",
                "User",
                "password456",
                "bidder1@example.com",
                new Date(95, Calendar.JUNE, 15),
                Country.United_Kingdom,
                "https://example.com/bidder1.jpg",
                Set.of(Role.USER)
        );
        userRepository.save(bidder1);

        bidder2 = new User(
                "Bidder",
                "Two",
                "User",
                "password789",
                "bidder2@example.com",
                new Date(85, Calendar.NOVEMBER, 20),
                Country.Canada,
                "https://example.com/bidder2.jpg",
                Set.of(Role.USER)
        );
        userRepository.save(bidder2);

        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterday = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        Date tomorrow = calendar.getTime();

        auction1 = new Auction(
                "Test Auction 1",
                "Description for auction 1",
                100,
                10,
                500,
                yesterday,
                tomorrow,
                false,
                seller,
                null
        );
        auctionRepository.save(auction1);

        Auction auction2 = new Auction(
                "Test Auction 2",
                "Description for auction 2",
                200,
                20,
                1000,
                yesterday,
                tomorrow,
                false,
                seller,
                null
        );
        auctionRepository.save(auction2);

        Bid bid1 = new Bid(auction1, bidder1, 150, "encrypted1", now);
        bidRepository.save(bid1);

        Bid bid2 = new Bid(auction2, bidder1, 250, "encrypted2", now);
        bidRepository.save(bid2);

        Bid bid3 = new Bid(auction1, bidder2, 160, "encrypted3", now);
        bidRepository.save(bid3);
    }

    @Test
    public void testGetAllByUser() {
        Page<Bid> bidder1Bids = bidRepository.getAllByUser(
                bidder1, PageRequest.of(0, 10));

        assertEquals(2, bidder1Bids.getTotalElements());
        assertTrue(bidder1Bids.getContent().stream()
                .allMatch(bid -> bid.getUser().equals(bidder1)));

        Page<Bid> bidder2Bids = bidRepository.getAllByUser(
                bidder2, PageRequest.of(0, 10));

        assertEquals(1, bidder2Bids.getTotalElements());
        assertEquals(auction1.getId(), bidder2Bids.getContent().get(0).getAuction().getId());
    }

    @Test
    public void testSaveAndFindById() {
        BidKey bidKey = new BidKey(auction1.getId(), bidder1.getId());

        Optional<Bid> foundBid = bidRepository.findById(bidKey);

        assertTrue(foundBid.isPresent());
        assertEquals(Integer.valueOf(150), foundBid.get().getAmount());
        assertEquals("encrypted1", foundBid.get().getEncryptedAmount());
    }

    @Test
    public void testBidUpdate() {
        BidKey bidKey = new BidKey(auction1.getId(), bidder1.getId());
        Optional<Bid> bidToUpdate = bidRepository.findById(bidKey);
        assertTrue(bidToUpdate.isPresent());

        Bid bid = bidToUpdate.get();
        bid.setAmount(180);
        bid.setEncryptedAmount("updated_encrypted");
        bidRepository.save(bid);

        Optional<Bid> updatedBid = bidRepository.findById(bidKey);
        assertTrue(updatedBid.isPresent());
        assertEquals(Integer.valueOf(180), updatedBid.get().getAmount());
        assertEquals("updated_encrypted", updatedBid.get().getEncryptedAmount());
    }
}
