package org.pwr.domain.images;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.pwr.domain.buckets.FileDetails;

import java.time.LocalDateTime;
import java.util.Optional;

public class ImageDataResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("shortDescription")
    private String shortDescription;

    private LocalDateTime lastUpdated;

    @JsonProperty("fileInfo")
    private FileDetails fileDetails;

    public ImageDataResponse(ImageDynamoEntity imageDynamoEntity) {
        id = imageDynamoEntity.getId();
        name = imageDynamoEntity.getName();
        shortDescription = imageDynamoEntity.getShortDescription();
        lastUpdated = imageDynamoEntity.getUpdatedAt();
        fileDetails = imageDynamoEntity.getFileDetails();
    }

    @JsonProperty("lastUpdated")
    public String getLastUpdatedAsString() {
        return Optional.ofNullable(lastUpdated).map(LocalDateTime::toString).orElse("NONE");
    }
}
