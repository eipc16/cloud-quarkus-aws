package org.pwr.domain.ocr.tesseract;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pwr.domain.buckets.FileDetails;
import org.pwr.infrastructure.config.TesseractConfiguration;
import org.pwr.infrastructure.exceptions.TesseractFunctionException;
import org.pwr.infrastructure.lambda.LambdaFunction;
import software.amazon.awssdk.http.HttpStatusFamily;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TesseractLambdaFunction extends LambdaFunction<FileDetails, TesseractResponse> {

    private TesseractConfiguration configuration;

    private TesseractLambdaFunction() {
        super();
        // empty for ApplicationScoped
    }

    @Inject
    public TesseractLambdaFunction(ObjectMapper objectMapper, TesseractConfiguration tesseractConfiguration) {
        super(objectMapper);
        this.configuration = tesseractConfiguration;
    }

    @Override
    protected String getName() {
        return configuration.getLambdaFunctionName();
    }

    @Override
    protected TesseractResponse handleResponse(InvokeResponse invokeResponse) throws JsonProcessingException {
        if (!HttpStatusFamily.of(invokeResponse.statusCode()).equals(HttpStatusFamily.SUCCESSFUL)) {
            throw new TesseractFunctionException(invokeResponse.functionError(), invokeResponse.logResult(), invokeResponse.statusCode());
        }
        String stringifiedResponse = invokeResponse.payload().asUtf8String();
        return getObjectMapper().readValue(stringifiedResponse, TesseractResponse.class);
    }
}
