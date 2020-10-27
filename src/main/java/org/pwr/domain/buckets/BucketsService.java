package org.pwr.domain.buckets;

import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;

public interface BucketsService {

    List<Bucket> getBucketList();

    List<S3Object> getBucketFiles(String bucketName);

    FileDetails uploadFile(String bucketName, MultipartBody file);

    StreamingResponse downloadFile(FileDetails fileDetails);
}
