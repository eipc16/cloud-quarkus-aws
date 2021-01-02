package org.pwr.infrastructure.config;

import io.quarkus.arc.config.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ConfigProperties(prefix = "tesseract")
public class TesseractConfiguration {

    private static final String DEFAULT_LAMBDA_FUNCTION_NAME = "tesseract-aws-lambda-dev-ocr";

    @ConfigProperty(name = "lambda-function.name", defaultValue = DEFAULT_LAMBDA_FUNCTION_NAME)
    public String lambdaFunctionName;

    public String getLambdaFunctionName() {
        return lambdaFunctionName;
    }
}
