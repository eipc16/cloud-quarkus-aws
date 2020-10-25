package org.pwr.domain.buckets;

import java.io.File;
import java.util.List;

public class BucketServiceImpl implements BucketsService{

    @Override
    public List<BucketInformation> getBucketList() {
        return null;
    }

    @Override
    public List<String> getBucketFiles(String bucketName) {
        return null;
    }

    @Override
    public FileDetails uploadFile(String bucketName, File file) {
        return null;
    }

    @Override
    public File downloadFile(FileDetails fileDetails) {
        return null;
    }
}
