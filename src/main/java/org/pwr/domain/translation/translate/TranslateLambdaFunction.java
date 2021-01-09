package org.pwr.domain.translation.translate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pwr.infrastructure.config.TranslateConfiguration;
import org.pwr.infrastructure.exceptions.TesseractFunctionException;
import org.pwr.infrastructure.lambda.LambdaFunction;
import software.amazon.awssdk.http.HttpStatusFamily;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TranslateLambdaFunction extends LambdaFunction<TranslateRequest, TranslateResponse> {

    private TranslateConfiguration configuration;

    private TranslateLambdaFunction() {
        super();
        // empty for ApplicationScoped
    }

    @Inject
    public TranslateLambdaFunction(ObjectMapper objectMapper, TranslateConfiguration translateConfiguration) {
        super(objectMapper);
        this.configuration = translateConfiguration;
    }

    @Override
    protected String getName() {
        return configuration.getLambdaFunctionName();
    }

    @Override
    protected TranslateRequest preprocessInput(TranslateRequest translateRequest) {
        TranslateRequest.Builder builder = TranslateRequest.builder(translateRequest);

        if(translateRequest.getSourceLanguage() == null) {
            builder.withSourceLanguage(configuration.getDefaultSourceLanguage());
        }

        if(translateRequest.getTargetLanguage() == null) {
            builder.withTargetLanguage(configuration.getDefaultTargetLanguage());
        }

        return builder.build();
    }

    @Override
    protected TranslateResponse handleResponse(InvokeResponse invokeResponse) throws JsonProcessingException {
        if (!HttpStatusFamily.of(invokeResponse.statusCode()).equals(HttpStatusFamily.SUCCESSFUL)) {
            throw new TesseractFunctionException(invokeResponse.functionError(), invokeResponse.logResult(), invokeResponse.statusCode());
        }
        String stringifiedResponse = invokeResponse.payload().asUtf8String();
        return getObjectMapper().readValue(stringifiedResponse, TranslateResponse.class);
    }
}
