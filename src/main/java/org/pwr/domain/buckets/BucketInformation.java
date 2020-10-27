package org.pwr.domain.buckets;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Optional;

public class BucketInformation {
    @JsonProperty
    private String bucketName;

    private LocalDateTime createDate;

    public BucketInformation(String bucketName, LocalDateTime createDate) {
        this.bucketName = bucketName;
        this.createDate = createDate;
    }

    @JsonProperty("createDate")
    public String getCreateDateAsString() {
        return Optional.ofNullable(createDate).map(LocalDateTime::toString).orElse("NONE");
    }
}
