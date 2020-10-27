package org.pwr.domain.buckets;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Dependent
public class BucketServiceImpl implements BucketsService {

    private final S3Client s3Client;
    private final S3ResourceHelper s3ResourceHelper;

    @Inject
    BucketServiceImpl(S3Client s3Client, S3ResourceHelper s3ResourceHelper) {
        this.s3Client = s3Client;
        this.s3ResourceHelper = s3ResourceHelper;
    }

    @Override
    public List<Bucket> getBucketList() {
        return s3Client.listBuckets().buckets();
    }

    @Override
    public List<S3Object> getBucketFiles(String bucketName) {
        ListObjectsRequest listRequest = ListObjectsRequest.builder().bucket(bucketName).build();
        return s3Client.listObjects(listRequest).contents().stream()
                .sorted(Comparator.comparing(S3Object::lastModified).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public FileDetails uploadFile(String bucketName, MultipartBody file) {
        PutObjectRequest request = s3ResourceHelper.buildPutRequest(bucketName, file);
        File fileToUpload = s3ResourceHelper.uploadToTemp(file.file);
        PutObjectResponse putResponse = s3Client.putObject(request, RequestBody.fromFile(fileToUpload));
        s3ResourceHelper.uploadToTemp(file.file);
        if (putResponse == null) {
            throw new IllegalStateException("Could not upload file.");
        }
        return new FileDetails(bucketName, request.key());
    }

    @Override
    public StreamingResponse downloadFile(FileDetails fileDetails) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GetObjectRequest request = s3ResourceHelper.buildGetRequest(fileDetails.getBucketName(), fileDetails.getObjectKey());
        GetObjectResponse response = s3Client.getObject(request, ResponseTransformer.toOutputStream(baos));
        return new StreamingResponse(response.contentType(), baos::writeTo);
    }
}
