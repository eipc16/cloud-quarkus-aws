package org.pwr.infrastructure.exceptions;

public abstract class AWSException extends RuntimeException {

    private static String relatedService;

    AWSException(Throwable throwable, String serviceName) {
        super(throwable.getMessage(), throwable);
        relatedService = serviceName;
    }

    public String getRelatedService() {
        return relatedService;
    }
}
