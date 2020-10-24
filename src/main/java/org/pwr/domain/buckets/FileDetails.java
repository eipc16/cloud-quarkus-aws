package org.pwr.domain.buckets;

public class FileDetails {

    private String bucketName;
    private String relativeUrl;

    public FileDetails(String bucketName, String relativeUrl) {
        this.bucketName = bucketName;
        this.relativeUrl = relativeUrl;
    }
}
