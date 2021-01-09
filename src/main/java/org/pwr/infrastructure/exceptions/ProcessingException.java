package org.pwr.infrastructure.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.text.MessageFormat;

public class ProcessingException extends WebApplicationException {

    public ProcessingException(JsonProcessingException jsonException) {
        super(getFinalMessage(jsonException), jsonException, Response.Status.INTERNAL_SERVER_ERROR);
    }

    private static String getFinalMessage(JsonProcessingException jsonException) {
        return MessageFormat.format("Encountered problems while decoding / encoding json. Cause: {0}", jsonException.getMessage());
    }
}
