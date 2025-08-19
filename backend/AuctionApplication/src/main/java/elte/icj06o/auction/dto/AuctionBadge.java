package elte.icj06o.auction.dto;

public class AuctionBadge {
    private Long id;
    private String title;
    private String frontPicture;
    private Long winnerId;

    public AuctionBadge(Long id, String title, String frontPicture, Long winnerId) {
        this.id = id;
        this.title = title;
        this.frontPicture = frontPicture;
        this.winnerId = winnerId;
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

    public String getFrontPicture() {
        return frontPicture;
    }

    public void setFrontPicture(String frontPicture) {
        this.frontPicture = frontPicture;
    }

    public Long getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Long winner) {
        this.winnerId = winner;
    }
}
