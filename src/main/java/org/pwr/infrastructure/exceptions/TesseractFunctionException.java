package org.pwr.infrastructure.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class TesseractFunctionException extends WebApplicationException {

    private String logs;
    private int statusCode;

    public TesseractFunctionException(String functionException, String logs, int statusCode) {
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
