package org.pwr.domain.images;

import org.pwr.domain.buckets.FileDetails;
import org.pwr.infrastructure.dynamodb.AttributeConverter;
import org.pwr.infrastructure.dynamodb.DynamoDBTable;
import org.pwr.infrastructure.dynamodb.convertes.FileDetailsConverter;
import org.pwr.infrastructure.dynamodb.convertes.LocalDateTimeConverter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.text.MessageFormat;
import java.time.LocalDateTime;

@DynamoDBTable(value = "images")
public class ImageDynamoEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String imageName;

    private String shortDescription;

    @AttributeConverter(LocalDateTimeConverter.class)
    private LocalDateTime uploadedAt;

    @AttributeConverter(FileDetailsConverter.class)
    private FileDetails fileDetails;

    public ImageDynamoEntity() {
        // empty for deserialization
    }

    public ImageDynamoEntity(Builder builder) {
        id = builder.id;
        imageName = builder.imageName;
        shortDescription = builder.shortDescription;
        uploadedAt = builder.uploadetAt;
        fileDetails = builder.fileDetails;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return imageName;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public LocalDateTime getUpdatedAt() {
        return uploadedAt;
    }

    public FileDetails getFileDetails() {
        return fileDetails;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ImageDynamoEntity imageDynamoEntity) {
        return new Builder(imageDynamoEntity);
    }

    @Override
    public String toString() {
        return MessageFormat.format("ID: {0}, Name: {1}, ShortDescription: {2}, UploadedAt: {3}", id, imageName, shortDescription, uploadedAt);
    }

    public static class Builder {
        private Long id;
        private String imageName;
        private String shortDescription;
        private LocalDateTime uploadetAt;
        private FileDetails fileDetails;

        public Builder() {
            // empty
        }

        public Builder(ImageDynamoEntity imageDynamoEntity) {
            id = imageDynamoEntity.id;
            imageName = imageDynamoEntity.imageName;
            shortDescription = imageDynamoEntity.shortDescription;
            uploadetAt = imageDynamoEntity.uploadedAt;
            fileDetails = imageDynamoEntity.fileDetails;
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withName(String imageName) {
            this.imageName = imageName;
            return this;
        }

        public Builder withShortDescription(String shortDescription) {
            this.shortDescription = shortDescription;
            return this;
        }

        public Builder withTime(LocalDateTime uploadetAt) {
            this.uploadetAt = uploadetAt;
            return this;
        }

        public Builder withFileDetails(FileDetails fileDetails) {
            this.fileDetails = fileDetails;
            return this;
        }

        public ImageDynamoEntity build() {
            return new ImageDynamoEntity(this);
        }
    }
}
