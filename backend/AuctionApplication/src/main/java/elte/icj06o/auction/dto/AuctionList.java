package elte.icj06o.auction.dto;

import java.util.Date;

public class AuctionList {
    private Long id;
    private String title;
    private String description;
    private Integer nextPrice;
    private Date startDate;
    private Date endDate;
    private Boolean isSecret;
    private UserBadge createdBy;
    private UserBadge winner;
    private String frontPicture;
    private Date createdTime;

    public AuctionList(Long id, String title, String description, Integer nextPrice,
                       Date startDate, Date endDate, Boolean isSecret, UserBadge createdBy,
                       UserBadge winner, String frontPicture, Date createdTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.nextPrice = nextPrice;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isSecret = isSecret;
        this.createdBy = createdBy;
        this.winner = winner;
        this.frontPicture = frontPicture;
        this.createdTime = createdTime;
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

    public String getFrontPicture() {
        return frontPicture;
    }

    public void setFrontPicture(String frontPicture) {
        this.frontPicture = frontPicture;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
