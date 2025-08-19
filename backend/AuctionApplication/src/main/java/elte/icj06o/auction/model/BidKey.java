package elte.icj06o.auction.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BidKey implements Serializable {
    @Column(name = "auction_id")
    private Long auctionId;

    @Column(name = "user_id")
    private Long userId;

    // Constructor
    public BidKey() {}

    public BidKey(Long auctionId, Long userId) {
        this.auctionId = auctionId;
        this.userId = userId;
    }

    // Getters and setters
    public Long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        BidKey bidKey = (BidKey) obj;
        return Objects.equals(auctionId, bidKey.auctionId)
                && Objects.equals(userId, bidKey.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(auctionId, userId);
    }
}
