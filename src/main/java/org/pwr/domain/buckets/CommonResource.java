package org.pwr.domain.buckets;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import software.amazon.awssdk.services.s3.model.GetObjectRequest;

abstract public class CommonResource {
    private final static String TEMP_DIR = System.getProperty("java.io.tmpdir");

    protected GetObjectRequest buildGetRequest(String bucketName, String objectKey) {
        return GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
    }

    protected File tempFilePath() {
        return new File(TEMP_DIR, "s3AsyncDownloadedTemp" +
                (new Date()).getTime() + UUID.randomUUID() +
                "." + ".tmp");
    }
}