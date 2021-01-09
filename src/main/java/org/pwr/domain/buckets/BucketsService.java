package org.pwr.domain.buckets;

import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface BucketsService {

    List<Bucket> getBucketList();

    List<S3Object> getBucketFiles(String bucketName);

    FileDetails uploadFile(String bucketName, MultipartBody file);

    FileDetails uploadFile(String bucketName, String objectKey, MultipartBody file);

    <T> T uploadFileAndThen(String bucketName, String objectKey, MultipartBody file, Function<FileDetails, T> mapper);

    StreamingResponse downloadFile(FileDetails fileDetails);
}
