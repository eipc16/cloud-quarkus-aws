package org.pwr.resteasy;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.text.MessageFormat;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Path("/resteasy/hello")
public class ExampleResource {

    @Inject
    private S3Client s3Client;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public List<String> hello() {
        return s3Client.listBuckets().buckets().stream()
                .map(this::getBucketAsString)
                .collect(Collectors.toList());
    }

    private String getBucketAsString(Bucket bucket) {
        return MessageFormat.format("Name: {0}, Creation Date: {1}",
                bucket.name(),
                bucket.creationDate().atZone(ZoneId.systemDefault()).toLocalDate());
    }
}