package org.pwr.infrastructure.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public abstract class AWSException extends WebApplicationException {

    private static String relatedService;

    AWSException(Throwable throwable, String serviceName) {
        super(throwable.getMessage(), throwable, Response.Status.INTERNAL_SERVER_ERROR);
        relatedService = serviceName;
    }

    public String getRelatedService() {
        return relatedService;
    }
}
