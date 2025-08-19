package elte.icj06o.auction.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "auctions")
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required.")
    @Size(max = 100, message = "The title should not be longer than 100 character.")
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Size(max = 1000, message = "The description should fit in 1000 characters.")
    @Column(name = "description", length = 1000)
    private String description;

    @NotNull(message = "Starting price is required.")
    @Positive(message = "Starting price has to be positive.")
    @Column(name = "starting_price", nullable = false)
    private Integer startingPrice;

    @Positive(message = "Bid increment has to be positive.")
    @Column(name = "bid_increment")
    private Integer bidIncrement;

    @Positive(message = "Buy now price has to be positive.")
    @Column(name = "buy_now_price")
    private Integer buyNowPrice;

    @NotNull(message = "The auction has to have a start date.")
    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @NotNull(message = "The auction has to have an end date.")
    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @Column(name = "is_secret", nullable = false)
    private Boolean isSecret;

    @ManyToOne
    @NotNull(message = "The auction needs an owner.")
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "winner")
    private User winner;

    @Column(name = "created_time", nullable = false)
    private Date createdTime;

    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Picture> pictures = new ArrayList<>();

    @OneToMany(mappedBy = "auction")
    private List<Bid> bids = new ArrayList<>();

    // Constructor
    public Auction() {}

    public Auction(String title, String description, Integer startingPrice,
                   Integer bidIncrement, Integer buyNowPrice, Date startDate,
                   Date endDate, Boolean isSecret, User createdBy, User winner) {
        this.title = title;
        this.description = description;
        this.startingPrice = startingPrice;
        this.bidIncrement = bidIncrement;
        this.buyNowPrice = buyNowPrice;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isSecret = isSecret;
        this.createdBy = createdBy;
        this.winner = winner;
        this.createdTime = new Date();
    }

    // Getters and setters
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

    public Integer getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(Integer startingPrice) {
        this.startingPrice = startingPrice;
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

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getWinner() {
        return winner;
    }

    public void setWinner(User winner) {
        this.winner = winner;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }

    public List<Bid> getBids() {
        return bids;
    }

    public void setBids(List<Bid> bids) {
        this.bids = bids;
    }
}
