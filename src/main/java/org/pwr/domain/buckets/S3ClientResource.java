package org.pwr.domain.buckets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

@Path("/buckets")
public class S3ClientResource {

    @Inject
    private BucketServiceImpl bucketsService;

    @GET
    @Path("{bucketName}/files/{objectKey}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@PathParam("bucketName") String bucketName, @PathParam("objectKey") String objectKey) {
        FileDetails details = new FileDetails(bucketName, objectKey);
        StreamingResponse output = bucketsService.downloadFile(details);
        return Response.ok(output.getOutput())
                .header("Content-Disposition", "attachment;filename=" + objectKey)
                .header("Content-Type", output.getContentType())
                .build();
    }

    @POST
    @Path("/{bucketName}/files")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public FileDetails uploadFile(@PathParam String bucketName,
                                  @MultipartForm MultipartBody file) {
        return bucketsService.uploadFile(bucketName, file);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<BucketInformation> listBuckets() {
        return bucketsService.getBucketList().stream()
                .map(bucket -> new BucketInformation(bucket.name(), LocalDateTime.ofInstant(bucket.creationDate(), ZoneOffset.UTC)))
                .collect(Collectors.toList());
    }


    @GET
    @Path("{bucketName}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<FileObject> listFiles(@PathParam("bucketName") String bucketName) {
        return bucketsService.getBucketFiles(bucketName).stream()
                .map(FileObject::from)
                .collect(Collectors.toList());
    }
}
