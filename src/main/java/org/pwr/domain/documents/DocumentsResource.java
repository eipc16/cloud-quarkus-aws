package org.pwr.domain.documents;

import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.pwr.domain.buckets.FileDownloadInformation;
import org.pwr.domain.buckets.StreamingResponse;
import org.pwr.infrastructure.dynamodb.DynamoPage;
import org.pwr.infrastructure.dynamodb.DynamoPaginable;
import org.pwr.infrastructure.identity.AuthorizationService;
import org.pwr.infrastructure.identity.UserGroups;
import org.pwr.infrastructure.identity.UserIdentity;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/documents")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@RequestScoped
public class DocumentsResource {

    @Inject
    DocumentsService documentsService;

    @Inject
    DocumentMapper documentMapper;

    @Inject
    UserIdentity userIdentity;

    @Inject
    AuthorizationService authorizationService;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public DocumentEntity uploadDocument(@MultipartForm DocumentData documentData) {
        authorizationService.checkPermissions(userIdentity, UserGroups.CLIENT);
        return documentsService.processDocument(documentData, userIdentity.getUserName());
    }

    @GET
    public DynamoPage<DocumentDTO> searchDocuments(@BeanParam DynamoPaginable paginable,
                                                      @BeanParam DocumentSearchFilter documentSearchFilter) {
        authorizationService.checkPermissions(userIdentity, UserGroups.CLIENT);
        return documentsService.getDocuments(paginable, documentSearchFilter).mapTo(documentMapper::toDTO);
    }

    @GET
    @Path("/{id}")
    public DocumentEntity getById(@PathParam String id) {
        authorizationService.checkPermissions(userIdentity, UserGroups.CLIENT);
        return documentsService.getDocumentById(id);
    }

    @PUT
    @Path("/{id}/ocrs")
    public DocumentEntity updateTextRecognition(@PathParam String id,
                                                DocumentUpdateTextRecognitionDTO documentUpdateTextRecognitionDTO) {
        authorizationService.checkPermissions(userIdentity, UserGroups.WORKER);
        return documentsService.updateTextRecognition(
                id,
                documentUpdateTextRecognitionDTO.getText(),
                userIdentity.getUserName()
        );
    }

    @PUT
    @Path("/{id}/translations")
    public DocumentEntity updateTranslation(@PathParam String id,
                                            DocumentUpdateTranslationDTO documentUpdateTranslationDTO) {
        authorizationService.checkPermissions(userIdentity, UserGroups.WORKER);
        return documentsService.updateTranslation(
                id,
                documentUpdateTranslationDTO.getText(),
                documentUpdateTranslationDTO.getSourceLanguage(),
                documentUpdateTranslationDTO.getTargetLanguage(),
                userIdentity.getUserName()
        );
    }

    @GET
    @Path("/{id}/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadDocument(@PathParam String id) {
        authorizationService.checkPermissions(userIdentity, UserGroups.CLIENT);
        FileDownloadInformation downloadInformation = documentsService.downloadDocument(id);
        StreamingResponse output = downloadInformation.getStreamingResponse();
        String originalFileName = downloadInformation.getFileDetails().getOriginalName().orElse(downloadInformation.getFileDetails().getObjectKey());
        return Response.ok(output.getOutput())
                .header("Content-Disposition", "attachment;filename=" + originalFileName)
                .header("Content-Type", output.getContentType())
                .build();
    }
}
