package elte.icj06o.auction.dto;

import java.util.Date;
import java.util.List;

public class InspectAuction {
    private Long id;
    private String title;
    private String description;
    private Integer nextPrice;
    private Integer bidIncrement;
    private Integer buyNowPrice;
    private Date startDate;
    private Date endDate;
    private Boolean isSecret;
    private UserBadge createdBy;
    private UserBadge winner;
    private List<String> pictures;
    private List<BidList> bids;

    public InspectAuction() { }

    public InspectAuction(Long id, String title, String description, Integer nextPrice,
                          Integer bidIncrement, Integer buyNowPrice, Date startDate,
                          Date endDate, Boolean isSecret, UserBadge createdBy, UserBadge winner,
                          List<String> pictures, List<BidList> bids) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.nextPrice = nextPrice;
        this.bidIncrement = bidIncrement;
        this.buyNowPrice = buyNowPrice;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isSecret = isSecret;
        this.createdBy = createdBy;
        this.winner = winner;
        this.pictures = pictures;
        this.bids = bids;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getNextPrice() {
        return nextPrice;
    }

    public void setNextPrice(Integer nextPrice) {
        this.nextPrice = nextPrice;
    }

    public Integer getBidIncrement() {
        return bidIncrement;
    }

    public void setBidIncrement(Integer bidIncrement) {
        this.bidIncrement = bidIncrement;
    }

    public Integer getBuyNowPrice() {
        return buyNowPrice;
    }

    public void setBuyNowPrice(Integer buyNowPrice) {
        this.buyNowPrice = buyNowPrice;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Boolean getIsSecret() {
        return isSecret;
    }

    public void setIsSecret(Boolean isSecret) {
        this.isSecret = isSecret;
    }

    public UserBadge getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserBadge createdBy) {
        this.createdBy = createdBy;
    }

    public UserBadge getWinner() {
        return winner;
    }

    public void setWinner(UserBadge winner) {
        this.winner = winner;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }

    public List<BidList> getBids() {
        return bids;
    }

    public void setBids(List<BidList> bids) {
        this.bids = bids;
    }
}
