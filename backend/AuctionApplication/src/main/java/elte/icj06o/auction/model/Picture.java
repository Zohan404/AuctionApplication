package elte.icj06o.auction.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

@Entity
@Table(name = "pictures")
public class Picture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    @NotBlank(message = "Picture URL is required.")
    @URL(regexp = "^.*\\.(jpg|jpeg|png|gif|bmp|webp|svg)$", message = "A picture must be uploaded!")
    @Column(name = "picture_url", nullable = false)
    private String pictureUrl;

    // Constructor
    public Picture() {}

    public Picture(Auction auction, String pictureUrl) {
        this.auction = auction;
        this.pictureUrl = pictureUrl;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }
}
