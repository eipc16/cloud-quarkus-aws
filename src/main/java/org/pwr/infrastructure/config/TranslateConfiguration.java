package org.pwr.infrastructure.config;

import io.quarkus.arc.config.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Optional;

@ConfigProperties(prefix = "translate")
public class TranslateConfiguration {

    private static final String DEFAULT_LAMBDA_FUNCTION_NAME = "translate-aws-lambda-dev-ocr";
    private static final double DEFAULT_INSUFFICIENT_CONFIDENCE_THRESHOLD = 0.90;

    @ConfigProperty(name = "lambda-function.name", defaultValue = DEFAULT_LAMBDA_FUNCTION_NAME)
    public String lambdaFunctionName;

    public String getLambdaFunctionName() {
        return lambdaFunctionName;
    }

}