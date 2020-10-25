package org.pwr.domain.buckets;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

@Path("/s3")
public class S3ClientResource extends CommonResource {
    @Inject
    S3Client s3;

    @GET
    @Path("{bucketName}/download/{objectKey}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@PathParam("bucketName") String bucketName, @PathParam("objectKey") String objectKey) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GetObjectResponse object = s3.getObject(buildGetRequest(bucketName, objectKey), ResponseTransformer.toOutputStream(baos));

        ResponseBuilder response = Response.ok((StreamingOutput) baos::writeTo);
        response.header("Content-Disposition", "attachment;filename=" + objectKey);
        response.header("Content-Type", object.contentType());
        return response.build();
    }

    @GET
    @Path("buckets")
    @Produces(MediaType.APPLICATION_JSON)
    public List<BucketInformation> listBuckets() {
        return s3.listBuckets().buckets().stream()
                .map(bucket -> new BucketInformation(bucket.name(), LocalDateTime.ofInstant(bucket.creationDate(), ZoneOffset.UTC)))
                .collect(Collectors.toList());
    }


    @GET
    @Path("{bucketName}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<FileObject> listFiles(@PathParam("bucketName") String bucketName) {
        ListObjectsRequest listRequest = ListObjectsRequest.builder().bucket(bucketName).build();

        //HEAD S3 objects to get metadata
        return s3.listObjects(listRequest).contents().stream().sorted(Comparator.comparing(S3Object::lastModified).reversed())
                .map(FileObject::from).collect(Collectors.toList());
    }
}
