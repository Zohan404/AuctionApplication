package elte.icj06o.auction.dto;

import java.util.List;

public class UserList {
    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String location;
    private String profilePicture;
    private List<Long> followers;
    private List<Long> followings;
    private List<Long> onGoingAuctions;
    private List<Long> soldItems;

    public UserList(Long id, String firstName, String middleName, String lastName,
                    String location, String profilePicture, List<Long> followers,
                    List<Long> followings, List<Long> onGoingAuctions, List<Long> soldItems) {
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.location = location;
        this.profilePicture = profilePicture;
        this.followers = followers;
        this.followings = followings;
        this.onGoingAuctions = onGoingAuctions;
        this.soldItems = soldItems;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public List<Long> getFollowers() {
        return followers;
    }

    public void setFollowers(List<Long> followers) {
        this.followers = followers;
    }

    public List<Long> getFollowings() {
        return followings;
    }

    public void setFollowings(List<Long> followings) {
        this.followings = followings;
    }

    public List<Long> getOnGoingAuctions() {
        return onGoingAuctions;
    }

    public void setOnGoingAuctions(List<Long> onGoingAuctions) {
        this.onGoingAuctions = onGoingAuctions;
    }

    public List<Long> getSoldItems() {
        return soldItems;
    }

    public void setSoldItems(List<Long> soldItems) {
        this.soldItems = soldItems;
    }
}
