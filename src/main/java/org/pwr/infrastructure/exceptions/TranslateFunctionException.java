package org.pwr.infrastructure.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class TranslateFunctionException extends WebApplicationException {

    private String logs;
    private int statusCode;

    public TranslateFunctionException(String functionException, String logs, int statusCode) {
        super(functionException, Response.Status.fromStatusCode(statusCode));
        this.logs = logs;
        this.statusCode = statusCode;
    }

    public String getLogs() {
        return logs;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
