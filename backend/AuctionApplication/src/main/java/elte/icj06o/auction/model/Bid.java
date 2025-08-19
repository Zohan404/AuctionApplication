package elte.icj06o.auction.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "bids")
public class Bid {
    @EmbeddedId
    private BidKey id;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "encrypted_amount", length = 1024)
    private String encryptedAmount;

    @Column(name = "time", nullable = false)
    private Date time;

    @ManyToOne
    @MapsId("auctionId")
    @JoinColumn(name = "auction_id")
    private Auction auction;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    public Bid() {}

    public Bid(Auction auction, User user, Integer amount, String encryptedAmount, Date time) {
        this.id = new BidKey(auction.getId(), user.getId());
        this.amount = amount;
        this.encryptedAmount = encryptedAmount;
        this.time = time;
        this.auction = auction;
        this.user = user;
    }

    public BidKey getId() {
        return id;
    }

    public void setId(BidKey id) {
        this.id = id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getEncryptedAmount() {
        return encryptedAmount;
    }

    public void setEncryptedAmount(String encryptedAmount) {
        this.encryptedAmount = encryptedAmount;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
