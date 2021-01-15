package org.pwr.domain.buckets;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BucketServiceTest {
    @Inject
    BucketServiceImpl service;

    @Inject
    S3ResourceHelper s3ResourceHelper;

    S3Client s3Client;
    List<Bucket> buckets;
    String bucketName;
    List<S3Object> files;

    @BeforeAll
    void setUp() {
        this.s3Client = S3Client.builder().region(Region.US_EAST_1).build();
        buckets = s3Client.listBuckets().buckets();
        bucketName = buckets.get(0).name();

        ListObjectsRequest listRequest = ListObjectsRequest.builder().bucket(bucketName).build();
        files = s3Client.listObjects(listRequest).contents().stream()
                .sorted(Comparator.comparing(S3Object::lastModified).reversed())
                .collect(Collectors.toList());
    }

    @Test
    public void testGetBucketList() {
        Assertions.assertEquals(buckets, service.getBucketList());
    }

    @Test
    public void testGetBucketFiles() {
        Assertions.assertEquals(files, service.getBucketFiles(bucketName));
    }

    @Test
    public void testDownloadFile() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileDetails fileDetails = new FileDetails(bucketName, files.get(0).key());
        GetObjectRequest request = s3ResourceHelper.buildGetRequest(bucketName, files.get(0).key());
        GetObjectResponse response = s3Client.getObject(request, ResponseTransformer.toOutputStream(baos));
        StreamingResponse streamingResponse = new StreamingResponse(response.contentType(), baos::writeTo);

        Assertions.assertEquals(streamingResponse.getContentType(), service.downloadFile(fileDetails).getContentType());
    }
}
