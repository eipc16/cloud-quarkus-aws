package org.pwr.documents;

import org.pwr.domain.buckets.BucketsService;
import org.pwr.domain.buckets.FileDetails;
import org.pwr.domain.buckets.MultipartBody;
import org.pwr.domain.buckets.StreamingResponse;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.function.Function;

public class TestBucketServiceImpl implements BucketsService {
    @Override
    public List<Bucket> getBucketList() {
        return null;
    }

    @Override
    public List<S3Object> getBucketFiles(String bucketName) {
        return null;
    }

    @Override
    public FileDetails uploadFile(String bucketName, MultipartBody file) {
        return null;
    }

    @Override
    public FileDetails uploadFile(String bucketName, String objectKey, MultipartBody file) {
        return new FileDetails(bucketName, file.fileName, objectKey);
    }

    @Override
    public <T> T uploadFileAndThen(String bucketName, String objectKey, MultipartBody file, Function<FileDetails, T> mapper) {
        FileDetails fileDetails = uploadFile(bucketName, objectKey, file);
        T result;
        result = mapper.apply(fileDetails);

        return result;
    }

    @Override
    public StreamingResponse downloadFile(FileDetails fileDetails) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        return new StreamingResponse("image/png", baos::writeTo);
    }
}
