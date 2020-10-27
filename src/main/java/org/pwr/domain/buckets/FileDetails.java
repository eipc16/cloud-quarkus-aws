package org.pwr.domain.buckets;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FileDetails {

    @JsonProperty("bucketName")
    private String bucketName;

    @JsonProperty("objectKey")
    private String objectKey;

    public FileDetails() {
        // Empty for deserialization
    }

    public FileDetails(String bucketName, String objectKey) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getObjectKey() {
        return objectKey;
    }
}
