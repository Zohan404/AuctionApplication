package elte.icj06o.auction.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import elte.icj06o.auction.crypto.PaillierCryptoService;
import elte.icj06o.auction.dto.AuctionList;
import elte.icj06o.auction.dto.BidList;
import elte.icj06o.auction.dto.InspectAuction;
import elte.icj06o.auction.model.Auction;
import elte.icj06o.auction.model.Bid;
import elte.icj06o.auction.model.Picture;
import elte.icj06o.auction.model.User;
import elte.icj06o.auction.service.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {
    private final AuctionService auctionService;
    private final PictureService pictureService;
    private final BidService bidService;
    private final UserService userService;
    private final PaillierCryptoService paillierCryptoService;
    private final Cloudinary cloudinary;

    public AuctionController(AuctionService auctionService, PictureService pictureService,
                             BidService bidService, UserService userService,
                             PaillierCryptoService paillierCryptoService, Cloudinary cloudinary) {
        this.auctionService = auctionService;
        this.pictureService = pictureService;
        this.bidService = bidService;
        this.userService = userService;
        this.paillierCryptoService = paillierCryptoService;
        this.cloudinary = cloudinary;
    }

    @GetMapping
    public List<AuctionList> getAllAuctions(
            @RequestParam(required = false, defaultValue = "999999") Integer maxNextPrice,
            @RequestParam(required = false, defaultValue = "999999") Integer maxBuyNowPrice,
            @RequestParam(required = false, defaultValue = "") String title,
            @RequestParam(required = false, defaultValue = "active,upcoming") Set<String> statuses,
            @RequestParam(required = false, defaultValue = "public,secret") Set<String> types,
            @RequestParam(required = false, defaultValue = "0") int page
    ) {
        Sort.Direction direction = Sort.Direction.fromString("desc");
        Sort sortBy = Sort.by(direction, "id");
        Pageable pageable = PageRequest.of(page, 12, sortBy);
        return auctionService.getAllFilteredAuctions(
                        maxNextPrice,
                        maxBuyNowPrice,
                        title,
                        statuses.contains("upcoming"),
                        statuses.contains("active"),
                        statuses.contains("closed"),
                        types.contains("public"),
                        types.contains("secret"),
                        pageable)
                .stream()
                .map(auction -> auctionService.auctionToAuctionList(auction,
                    userService.userToUserBadge(auction.getCreatedBy()),
                    auction.getWinner() == null ? null : userService.userToUserBadge(auction.getWinner())))
                .toList();
    }

    @GetMapping("/byUser/{userId}")
    public List<AuctionList> getAuctionsByUserId(@PathVariable Long userId,
                                                 @RequestParam(required = false, defaultValue = "0") int page) {
        Sort.Direction direction = Sort.Direction.fromString("desc");
        Sort sortBy = Sort.by(direction, "id");
        Pageable pageable = PageRequest.of(page, 12, sortBy);

        User user = userService.getUserById(userId).orElseThrow(() -> new RuntimeException("User not found."));
        
        return auctionService.getAuctionsByUser(user, pageable)
                .stream()
                .map(auction -> auctionService.auctionToAuctionList(auction, null,
                        auction.getWinner() == null ? null : userService.userToUserBadge(auction.getWinner())))
                .toList();
    }

    @GetMapping("/byFollowings")
    public List<AuctionList> getAuctionsByUserId(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestParam(defaultValue = "0") int page) {
        Sort.Direction direction = Sort.Direction.fromString("desc");
        Sort sortBy = Sort.by(direction, "id");
        Pageable pageable = PageRequest.of(page, 12, sortBy);

        User user = userService.getUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found."));

        return auctionService.getAuctionsByUserFollowers(user, pageable)
                .stream()
                .map(auction -> auctionService.auctionToAuctionList(auction,
                        userService.userToUserBadge(auction.getCreatedBy()),
                        auction.getWinner() == null ? null : userService.userToUserBadge(auction.getWinner())))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InspectAuction> getAuctionById(@PathVariable Long id) {
        return auctionService.getAuctionById(id)
                .map(auction -> ResponseEntity.ok(auctionService.auctionToInspectAuction(
                        auction,
                        userService.userToUserBadge(auction.getCreatedBy()),
                        auction.getWinner() == null ? null : userService.userToUserBadge(auction.getWinner()),
                        auction.getBids()
                                .stream()
                                .map(bid -> bidService.bidToBidList(bid, userService.userToUserBadge(bid.getUser()), null))
                                .toList()
                )))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createAuction(
            @Valid @RequestPart("auction") Auction auction,
            @RequestPart("files") List<MultipartFile> files,
            @AuthenticationPrincipal UserDetails userDetails)
    throws IOException{
        if (files.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<User> user = userService.getUserByEmail(userDetails.getUsername());
        if (user.isPresent()){
            auctionService.createAuction(auction, user.get());
            for (MultipartFile file : files) {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                String imageUrl = (String) uploadResult.get("secure_url");
                pictureService.uploadPicture(new Picture(auction, imageUrl));
            }
        } else {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/bids/byUser/{userId}")
    public List<BidList> getBidsByUser(@PathVariable Long userId,
                                       @RequestParam(defaultValue = "0") int page) {
        Sort.Direction direction = Sort.Direction.fromString("desc");
        Sort sortBy = Sort.by(direction, "id");
        Pageable pageable = PageRequest.of(page, 10, sortBy);

        User user = userService.getUserById(userId).orElseThrow(() -> new RuntimeException("User not found."));

        return bidService.getBidsByUser(user, pageable)
                .map(bid -> bidService.bidToBidList(bid, null, auctionService.auctionToAuctionBadge(bid.getAuction())))
                .toList();
    }

    @PostMapping("/{id}/bid")
    public ResponseEntity<?> bidAuction(@PathVariable Long id,
                                       @RequestBody Integer bidPrice,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Auction> optionalAuction = auctionService.getAuctionById(id);
        Optional<User> optionalUser = userService.getUserByEmail(userDetails.getUsername());
        if (optionalAuction.isPresent() && optionalUser.isPresent()) {
            Auction auction = optionalAuction.get();
            User user = optionalUser.get();

            if (auction.getCreatedBy().getId().equals(user.getId())) {
                return ResponseEntity.badRequest().body("You cannot bid on your own auction.");
            }

            try {
                Bid bid;
                if (auction.getIsSecret()) {
                    bid = bidService.handleSecretBid(auction, user, bidPrice);
                } else {
                    bid = bidService.handlePublicBid(auction, user, bidPrice);
                    if (auction.getBuyNowPrice().equals(bidPrice)) {
                        auction.setWinner(bid.getUser());
                    }
                }

                return ResponseEntity.ok(bid);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/buy")
    public ResponseEntity<?> buyNowAuction(@PathVariable Long id,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Auction> optionalAuction = auctionService.getAuctionById(id);
        Optional<User> optionalUser = userService.getUserByEmail(userDetails.getUsername());
        if (optionalAuction.isPresent() && optionalUser.isPresent()) {
            Auction auction = optionalAuction.get();
            User user = optionalUser.get();

            if (auction.getCreatedBy().getId().equals(user.getId())) {
                return ResponseEntity.badRequest().body("You cannot bid on your own auction.");
            }

            try {
                Bid bid = bidService.handlePublicBid(auction, user, auction.getBuyNowPrice());
                auctionService.processPublicAuction(auction);
                return ResponseEntity.ok(bid);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/crypto/publicKey")
    public ResponseEntity<PaillierCryptoService.PublicKeyData> getPublicKey() {
        return ResponseEntity.ok(paillierCryptoService.getPublicKeyData());
    }
}
