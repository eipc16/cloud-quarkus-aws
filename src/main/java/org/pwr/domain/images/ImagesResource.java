package org.pwr.domain.images;

import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.pwr.domain.buckets.MultipartBody;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/images")
public class ImagesResource {

    @Inject
    ImagesService imagesService;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public ImageDataResponse uploadImage(@MultipartForm ImageData imageData) {
        ImageDynamoEntity entity = imagesService.uploadImage(imageData);
        return new ImageDataResponse(entity);
    }

    @PUT
    @Path("/{imageId}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public ImageDataResponse updateImage(@PathParam Long imageId,
                                         @MultipartForm ImageData imageData) {
        ImageDynamoEntity entity = imagesService.updateImage(imageId, imageData);
        return new ImageDataResponse(entity);
    }

    @GET
    @Path("/{imageId}")
    @Produces(MediaType.APPLICATION_JSON)
    public ImageDataResponse getImageById(@PathParam Long imageId) {
        ImageDynamoEntity entity = imagesService.getImageById(imageId);
        return new ImageDataResponse(entity);
    }

    @DELETE
    @Path("/{imageId}")
    public Response deleteImageById(@PathParam Long imageId) {
        imagesService.removeImageById(imageId);
        return Response.ok().build();
    }
}
