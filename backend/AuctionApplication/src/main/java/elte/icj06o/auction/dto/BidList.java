package elte.icj06o.auction.dto;

import java.util.Date;

public class BidList {
    private UserBadge user;
    private AuctionBadge auction;
    private Integer amount;
    private Date time;

    public BidList() { }

    public BidList(UserBadge user, AuctionBadge auction, Integer amount, Date time) {
        this.user = user;
        this.auction = auction;
        this.amount = amount;
        this.time = time;
    }

    public UserBadge getUser() {
        return user;
    }

    public void setUser(UserBadge user) {
        this.user = user;
    }

    public AuctionBadge getAuction() {
        return auction;
    }

    public void setAuction(AuctionBadge auction) {
        this.auction = auction;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
