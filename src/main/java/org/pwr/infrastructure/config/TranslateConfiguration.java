package org.pwr.infrastructure.config;

import io.quarkus.arc.config.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Optional;

@ConfigProperties(prefix = "translate")
public class TranslateConfiguration {

    private static final String DEFAULT_LAMBDA_FUNCTION_NAME = "translate-aws-lambda-dev-ocr";
    private static final String DEFAULT_INSUFFICIENT_CONFIDENCE_THRESHOLD = "0.9";

    @ConfigProperty(name = "lambda-function.name", defaultValue = DEFAULT_LAMBDA_FUNCTION_NAME)
    public String lambdaFunctionName;

    @ConfigProperty(name = "default.threshold", defaultValue = DEFAULT_INSUFFICIENT_CONFIDENCE_THRESHOLD)
    public Double defaultThreshold;

    @ConfigProperty(name = "default.sourceLang", defaultValue = "en")
    public String defaultSourceLang;

    @ConfigProperty(name = "default.targetLang", defaultValue = "pl")
    public String defaultTargetLang;

    public String getLambdaFunctionName() {
        return lambdaFunctionName;
    }

    public Double getDefaultThreshold() {
        return defaultThreshold;
    }

    public String getDefaultSourceLanguage() {
        return defaultSourceLang;
    }

    public String getDefaultTargetLanguage() {
        return defaultTargetLang;
    }
}