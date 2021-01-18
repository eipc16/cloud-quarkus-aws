package org.pwr.documents;

import org.pwr.domain.buckets.BucketsService;
import org.pwr.domain.buckets.FileDetails;
import org.pwr.domain.buckets.MultipartBody;
import org.pwr.domain.buckets.StreamingResponse;
import org.pwr.infrastructure.qualifiers.TestBean;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.function.Function;

@TestBean
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
        return new FileDetails(bucketName, file.fileName);
    }

    @Override
    public FileDetails uploadFile(String bucketName, String objectKey, MultipartBody file) {
        String originalName = String.valueOf(file.fileName);
        file.fileName = objectKey;
        FileDetails fileDetails = uploadFile(bucketName, file);
        file.fileName = originalName;
        return new FileDetails(fileDetails.getBucketName(), originalName, fileDetails.getObjectKey());
    }

    @Override
    public <T> T uploadFileAndThen(String bucketName, String objectKey, MultipartBody file, Function<FileDetails, T> mapper) {
        FileDetails fileDetails = uploadFile(bucketName, objectKey, file);
        T result;
        result = mapper.apply(fileDetails);

        if (result == null) {
            throw new RuntimeException("Result shouldn't be null at this point!");
        }

        return result;
    }

    @Override
    public StreamingResponse downloadFile(FileDetails fileDetails) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        return new StreamingResponse("image/png", baos::writeTo);
    }
}
