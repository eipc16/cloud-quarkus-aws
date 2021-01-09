package org.pwr.domain.buckets;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.UUID;

import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;

@Dependent
public class S3ResourceHelper {
    private final static String TEMP_DIR = System.getProperty("java.io.tmpdir");

    protected PutObjectRequest buildPutRequest(String bucketName, MultipartBody file) {
        return PutObjectRequest.builder()
                .bucket(bucketName)
                .key(file.fileName)
                .contentType(file.mimeType)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();
    }

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

    protected File uploadToTemp(InputStream data) {
        File tempPath;
        try {
            tempPath = File.createTempFile("uploadS3Tmp", ".tmp");
            Files.copy(data, tempPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return tempPath;
    }

    DeleteObjectRequest buildDeleteRequest(String bucketName, String objectKey) {
        return DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
    }
}
