package elte.icj06o.auction.model;

import elte.icj06o.auction.config.NotAllowedEnumValue;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

import java.util.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required.")
    @Pattern(regexp = "[A-Z].*", message = "First name needs to start with capital.")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Pattern(regexp = "([A-Z].*)?", message = "Middle name needs to start with capital.")
    @Column(name = "middle_name")
    private String middleName;

    @NotBlank(message = "Last name is required.")
    @Pattern(regexp = "[A-Z].*", message = "Last name needs to start with capital.")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank(message = "Password is required.")
    @Size(min = 6, message = "Password must be at least 6 characters long.")
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email should be valid.")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotNull(message = "Date of birth is required.")
    @Past(message = "User must exist.")
    @Column(name = "date_of_birth", nullable = false)
    private Date dateOfBirth;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Location is required.")
    @NotAllowedEnumValue(enumClass = Country.class, prohibited = "NO_COUNTRY", message = "Location is required.")
    @Column(name = "location", nullable = false)
    private Country location;

    @URL(regexp = "^.*\\.(jpg|jpeg|png|gif|bmp|webp|svg)$", message = "A picture must be uploaded!")
    @Column(name = "profile_picture")
    private String profilePicture;

    @OneToMany(mappedBy = "createdBy")
    private List<Auction> createdAuctions = new ArrayList<>();

    @OneToMany(mappedBy = "winner")
    private List<Auction> winedAuctions = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Bid> bids = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "followers_table",
            joinColumns = @JoinColumn(name = "following_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id")
    )
    private Set<User> followers = new HashSet<>();

    @ManyToMany(mappedBy = "followers")
    private Set<User> followings = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", nullable = false))
    private Set<Role> roles = new HashSet<>();

    // Constructor
    public User() {}

    public User(String firstName, String middleName, String lastName,
                String password, String email, Date dateOfBirth,
                Country location, String profilePicture, Set<Role> roles) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.location = location;
        this.profilePicture = profilePicture;
        this.roles = roles;
    }

    // Getters and setters
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Country getLocation() {
        return location;
    }

    public void setLocation(Country location) {
        this.location = location;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public List<Auction> getCreatedAuctions() {
        return createdAuctions;
    }

    public void setCreatedAuctions(List<Auction> createdAuctions) {
        this.createdAuctions = createdAuctions;
    }

    public List<Auction> getWinedAuctions() {
        return winedAuctions;
    }

    public void setWinedAuctions(List<Auction> winedAuctions) {
        this.winedAuctions = winedAuctions;
    }

    public List<Bid> getBids() {
        return bids;
    }

    public void setBids(List<Bid> bids) {
        this.bids = bids;
    }

    public Set<User> getFollowings() {
        return followings;
    }

    public void setFollowings(Set<User> followings) {
        this.followings = followings;
    }

    public Set<User> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<User> followers) {
        this.followers = followers;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
