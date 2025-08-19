package elte.icj06o.auction.service;

import elte.icj06o.auction.model.Picture;
import elte.icj06o.auction.repository.PictureRepository;
import org.springframework.stereotype.Service;

@Service
public class PictureServiceImpl implements PictureService {
    private final PictureRepository pictureRepository;

    public PictureServiceImpl(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
    }

    public void uploadPicture(Picture picture) {
        picture.setId(null);
        pictureRepository.save(picture);
    }
}
