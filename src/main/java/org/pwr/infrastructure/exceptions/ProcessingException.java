package org.pwr.infrastructure.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.text.MessageFormat;

public class ProcessingException extends RuntimeException {

    public ProcessingException(JsonProcessingException jsonException) {
        super(getFinalMessage(jsonException), jsonException);
    }

    private static String getFinalMessage(JsonProcessingException jsonException) {
        return MessageFormat.format("Encountered problems while decoding / encoding json. Cause: {0}", jsonException.getMessage());
    }
}
