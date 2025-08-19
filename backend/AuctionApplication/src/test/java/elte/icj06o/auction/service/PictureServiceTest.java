package elte.icj06o.auction.service;

import elte.icj06o.auction.model.Auction;
import elte.icj06o.auction.model.Picture;
import elte.icj06o.auction.repository.PictureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PictureServiceTest {
    @Mock
    private PictureRepository pictureRepository;

    @InjectMocks
    private PictureServiceImpl pictureService;

    @Captor
    private ArgumentCaptor<Picture> pictureCaptor;

    private Picture picture;
    private Auction auction;

    @BeforeEach
    void setUp() {
        auction = new Auction();
        auction.setId(1L);

        picture = new Picture();
        picture.setId(1L);
        picture.setAuction(auction);
        picture.setPictureUrl("http://example.com/image.jpg");
    }

    @Test
    void uploadPicture_ShouldSetIdToNullAndSave() {
        pictureService.uploadPicture(picture);

        verify(pictureRepository).save(pictureCaptor.capture());
        Picture savedPicture = pictureCaptor.getValue();

        assertNull(savedPicture.getId());
        assertEquals(auction, savedPicture.getAuction());
        assertEquals("http://example.com/image.jpg", savedPicture.getPictureUrl());
    }

    @Test
    void uploadPicture_WithNullId_ShouldStillSave() {
        picture.setId(null);

        pictureService.uploadPicture(picture);

        verify(pictureRepository).save(picture);
    }
}
