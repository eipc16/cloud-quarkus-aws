package org.pwr.domain.buckets;

import java.time.LocalDateTime;

public class BucketInformation {

    private String bucketName;
    private LocalDateTime createDate;

    public BucketInformation(String bucketName, LocalDateTime createDate) {
        this.bucketName = bucketName;
        this.createDate = createDate;
    }
}
