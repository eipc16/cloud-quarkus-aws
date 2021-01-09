package org.pwr.domain.buckets;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public class FileDetails {

    @JsonProperty("bucketName")
    private String bucketName;

    @JsonProperty("objectKey")
    private String objectKey;

    @JsonProperty("originalName")
    private String originalName;

    public FileDetails() {
        // Empty for deserialization
    }

    public FileDetails(String bucketName, String objectKey) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }

    public FileDetails(String bucketName, String originalName, String objectKey) {
        this(bucketName, objectKey);
        this.originalName = originalName;
    }

    @JsonIgnore
    public String getBucketName() {
        return bucketName;
    }

    @JsonIgnore
    public String getObjectKey() {
        return objectKey;
    }

    @JsonIgnore
    public Optional<String> getOriginalName() {
        return Optional.ofNullable(originalName);
    }
}
