package org.pwr.domain.buckets;

import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @GET
    @Path("{bucketName}/{fileName}/text")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TextDetectionDetail> detectText(@PathParam("bucketName") String bucketName, @PathParam("fileName") String fileName) {
        RekognitionClient rekognitionClient = RekognitionClient.builder()
                .region(Region.US_EAST_1)
                .build();

        DetectTextRequest request = DetectTextRequest.builder().image(
                Image.builder().
                        s3Object(S3Object.builder()
                                .bucket(bucketName)
                                .name(fileName)
                                .build())
                        .build())
                .build();

        DetectTextResponse result = rekognitionClient.detectText(request);

        List<TextDetection> textDetections = result.textDetections();
        List<TextDetectionDetail> textDetectionDetails = new ArrayList<>();
        for (TextDetection text : textDetections) {
            if(text.parentId() == null) {
                textDetectionDetails.add(new TextDetectionDetail(text));
            }
        }

        return textDetectionDetails;
    }

    @GET
    @Path("rekognition")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Label> getRekognition() {
        RekognitionClient client = RekognitionClient.builder()
                .region(Region.US_EAST_1)
                .build();
        DetectLabelsResponse detectLabelsResponse = client.detectLabels(DetectLabelsRequest.builder()
                .image(Image.builder()
                        .s3Object(S3Object.builder()
                                .bucket("bitbeat-bucket")
                                .name("sqL_inj_accu.png")
                                .build())
                        .build())
                .build());
        return detectLabelsResponse.labels();
    }
}
