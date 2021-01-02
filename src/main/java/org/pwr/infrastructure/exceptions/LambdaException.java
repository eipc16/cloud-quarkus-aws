package org.pwr.infrastructure.exceptions;

public class LambdaException extends AWSException {
    public LambdaException(Throwable throwable) {
        super(throwable, "lambda");
    }
}
