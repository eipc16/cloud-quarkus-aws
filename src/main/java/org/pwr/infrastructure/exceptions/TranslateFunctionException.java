package org.pwr.infrastructure.exceptions;

public class TranslateFunctionException extends RuntimeException {

    private String logs;
    private int statusCode;

    public TranslateFunctionException(String functionException, String logs, int statusCode) {
        super(functionException);
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
