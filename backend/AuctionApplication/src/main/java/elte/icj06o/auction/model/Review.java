package elte.icj06o.auction.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

@Entity
@Table(name = "reviews")
public class Review {
    @EmbeddedId
    private ReviewKey id;

    @ManyToOne
    @MapsId("fromUserId")
    @JoinColumn(name = "from_id")
    private User fromUser;

    @ManyToOne
    @MapsId("toUserId")
    @JoinColumn(name = "to_id")
    private User toUser;

    @NotBlank(message = "Review message is required.")
    @Column(name = "text", nullable = false)
    private String text;

    @NotNull(message = "Rating is required.")
    @Min(value = 1, message = "Rating must be at least 1.")
    @Max(value = 5, message = "Rating must be maximum 5.")
    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "created_time", nullable = false)
    private Date createdTime;

    public Review() { }

    public Review(User fromUser, User toUser, String text, Integer rating) {
        this.id = new ReviewKey(fromUser.getId(), toUser.getId());
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.text = text;
        this.rating = rating;
        this.createdTime = new Date();
    }

    public ReviewKey getId() {
        return id;
    }

    public void setId(ReviewKey id) {
        this.id = id;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
