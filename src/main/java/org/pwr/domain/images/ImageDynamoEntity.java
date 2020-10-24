package org.pwr.domain.images;

import org.pwr.infrastructure.dynamodb.AttributeConverter;
import org.pwr.infrastructure.dynamodb.DynamoDBTable;
import org.pwr.infrastructure.dynamodb.convertes.LocalDateTimeConverter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@DynamoDBTable(name = "images", forceRebuild = true)
public class ImageDynamoEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String imageName;

    private String shortDescription;

    @AttributeConverter(LocalDateTimeConverter.class)
    private LocalDateTime uploadetAt;

    private List<Long> testListString;

    public ImageDynamoEntity() {
        // empty for deserialization
    }

    public ImageDynamoEntity(Builder builder) {
        id = builder.id;
        imageName = builder.imageName;
        shortDescription = builder.shortDescription;
        uploadetAt = builder.uploadetAt;
        testListString = builder.listString;
    }

    public Long getId() {
        return id;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return MessageFormat.format("ID: {0}, Name: {1}, ShortDescription: {2}, UploadedAt: {3}, List String: {4}", id, imageName, shortDescription, uploadetAt, testListString);
    }

    public static class Builder {
        private Long id;
        private String imageName;
        private String shortDescription;
        private LocalDateTime uploadetAt;
        private List<Long> listString = new ArrayList<>();

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

        public Builder withListString(Collection<Long> listString) {
            this.listString.addAll(listString);
            return this;
        }

        public ImageDynamoEntity build() {
            return new ImageDynamoEntity(this);
        }
    }
}
