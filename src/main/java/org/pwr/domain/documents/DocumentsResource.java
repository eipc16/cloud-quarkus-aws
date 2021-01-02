package org.pwr.domain.documents;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/documents")
public class DocumentsResource {

    @Inject
    DocumentsService documentsService;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> uploadDocument(@MultipartForm DocumentData documentData) {
        return documentsService.processDocument(documentData);
    }
}
