package org.pwr.domain.buckets;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class BucketInformation {
    @JsonProperty
    private String bucketName;
    @JsonProperty
    private LocalDateTime createDate;

    public BucketInformation(String bucketName, LocalDateTime createDate) {
        this.bucketName = bucketName;
        this.createDate = createDate;
    }
}
