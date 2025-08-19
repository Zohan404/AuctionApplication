package elte.icj06o.auction.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ReviewKey implements Serializable {
    @Column(name = "from_id")
    private Long fromUserId;

    @Column(name = "to_id")
    private Long toUserId;

    // Constructor
    public ReviewKey() {}

    public ReviewKey(Long fromUserId, Long toUserId) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
    }

    // Getters and setters
    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        ReviewKey reviewKey = (ReviewKey) obj;
        return Objects.equals(fromUserId, reviewKey.fromUserId)
                && Objects.equals(toUserId, reviewKey.toUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromUserId, toUserId);
    }
}
