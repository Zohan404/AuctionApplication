package elte.icj06o.auction.dto;

public class ReviewList {
    private UserBadge user;
    private String text;
    private Integer rating;

    public ReviewList() { }

    public ReviewList(UserBadge user, String text, Integer rating) {
        this.user = user;
        this.text = text;
        this.rating = rating;
    }

    public UserBadge getUser() {
        return user;
    }

    public void setUser(UserBadge user) {
        this.user = user;
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
}
