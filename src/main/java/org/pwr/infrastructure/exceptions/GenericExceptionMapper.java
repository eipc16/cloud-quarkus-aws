package org.pwr.infrastructure.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.pwr.infrastructure.config.ApplicationConfiguration;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    @Inject
    private ApplicationConfiguration applicationConfiguration;

    @Inject
    ObjectMapper objectMapper;

    @Override
    public Response toResponse(Throwable throwable) {
        if(throwable.getCause() instanceof WebApplicationException) {
            WebApplicationException exception = (WebApplicationException) throwable.getCause();
            Response exceptionResponse = exception.getResponse();
            return Response.status(exceptionResponse.getStatusInfo())
                    .entity(getFinalException(exception, exceptionResponse.getStatusInfo().getStatusCode()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(getFinalException(throwable, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                .build();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getFinalException(Throwable throwable, int statusCode) {
        Map<String, Object> exceptionMap = objectMapper.convertValue(throwable, Map.class);
        exceptionMap.remove("stackTrace");
        exceptionMap.remove("localizedMessage");
        exceptionMap.remove("suppressed");
        exceptionMap.remove("response");
        exceptionMap.remove("cause");

        exceptionMap.put("status", statusCode);

        if(throwable.getCause() != null) {
            String message = throwable.getClass().getName() + ": " + throwable.getCause().getMessage();
            exceptionMap.put("cause", message);
        }

        if(applicationConfiguration.isDebugEnabled()) {
            exceptionMap.put("stackTrace", getCompressedStackTrace(throwable));
        }

        return exceptionMap;
    }

    private List<String> getCompressedStackTrace(Throwable throwable) {
        if(throwable.getStackTrace() == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(throwable.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toUnmodifiableList());
    }
}
