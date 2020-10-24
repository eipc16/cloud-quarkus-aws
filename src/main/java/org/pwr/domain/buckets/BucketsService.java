package org.pwr.domain.buckets;

import java.io.File;
import java.util.List;

public interface BucketsService {

    List<BucketInformation> getBucketList();

    List<String> getBucketFiles(String bucketName);

    FileDetails uploadFile(String bucketName, File file);

    File downloadFile(FileDetails fileDetails);
}
