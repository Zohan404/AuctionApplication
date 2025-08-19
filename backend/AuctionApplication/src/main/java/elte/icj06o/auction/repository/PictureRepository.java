package elte.icj06o.auction.repository;

import elte.icj06o.auction.model.Picture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PictureRepository extends JpaRepository<Picture, Long> {
}
