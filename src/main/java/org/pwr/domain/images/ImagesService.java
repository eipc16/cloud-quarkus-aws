package org.pwr.domain.images;

import org.pwr.domain.buckets.BucketServiceImpl;
import org.pwr.domain.buckets.FileDetails;
import org.pwr.domain.buckets.MultipartBody;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.time.LocalDateTime;

@Dependent
public class ImagesService {

    private final ImagesRepository imagesRepository;
    private final BucketServiceImpl bucketsService;

    private static final String DEFAULT_BUCKET = "bitbeat-bucket";

    @Inject
    ImagesService(ImagesRepository imagesRepository, BucketServiceImpl bucketsService) {
        this.imagesRepository = imagesRepository;
        this.bucketsService = bucketsService;
    }

    public ImageDynamoEntity uploadImage(ImageData imageData) {
        FileDetails fileDetails = bucketsService.uploadFile(DEFAULT_BUCKET, imageData);
        return imagesRepository.saveImage(ImageDynamoEntity.builder()
                .withName(imageData.getName())
                .withShortDescription(imageData.getShortDescription())
                .withTime(LocalDateTime.now())
                .withFileDetails(fileDetails)
                .build());
    }

    public ImageDynamoEntity updateImage(Long imageId, ImageData imageData) {
        FileDetails fileDetails = bucketsService.uploadFile(DEFAULT_BUCKET, imageData);
        ImageDynamoEntity existingEntity = getImageById(imageId);
        return imagesRepository.saveImage(ImageDynamoEntity.builder(existingEntity)
                .withName(imageData.getName())
                .withShortDescription(imageData.getShortDescription())
                .withTime(LocalDateTime.now())
                .withFileDetails(fileDetails)
                .build());
    }

    public ImageDynamoEntity getImageById(Long id) {
        return imagesRepository.getById(id);
    }

    public boolean removeImageById(Long id) {
        return imagesRepository.deleteImage(id);
    }
}
