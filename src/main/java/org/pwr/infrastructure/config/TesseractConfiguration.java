package org.pwr.infrastructure.config;

import io.quarkus.arc.config.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ConfigProperties(prefix = "tesseract")
public class TesseractConfiguration {

    private static final String DEFAULT_LAMBDA_FUNCTION_NAME = "tesseract-aws-lambda-dev-ocr";
    private static final String DEFAULT_INSUFFICIENT_CONFIDENCE_THRESHOLD = "0.9";

    @ConfigProperty(name = "lambda-function.name", defaultValue = DEFAULT_LAMBDA_FUNCTION_NAME)
    public String lambdaFunctionName;

    @ConfigProperty(name = "default.threshold", defaultValue = DEFAULT_INSUFFICIENT_CONFIDENCE_THRESHOLD)
    public Double defaultThreshold;

    public String getLambdaFunctionName() {
        return lambdaFunctionName;
    }

    public Double getDefaultThreshold() {
        return defaultThreshold;
    }
}
