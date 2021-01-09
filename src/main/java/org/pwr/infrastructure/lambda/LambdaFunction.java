package org.pwr.infrastructure.lambda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.common.constraint.Nullable;
import org.pwr.infrastructure.exceptions.LambdaException;
import org.pwr.infrastructure.exceptions.ProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.services.lambda.model.ServiceException;

import javax.inject.Inject;

public abstract class LambdaFunction<I, O> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LambdaFunction.class);

    private ObjectMapper objectMapper;
    private LambdaClient lambdaClient;

    protected LambdaFunction() {
        // empty for ApplicationScoped
    }

    @Inject
    public LambdaFunction(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.lambdaClient = LambdaClient.builder().region(Region.US_EAST_1).build();
    }

    protected abstract String getName();

    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    protected I preprocessInput(I input) {
        return input;
    }

    public @Nullable
    O apply(I payload) {
        O response = null;
        try {
            InvokeRequest request = getInvokeRequest(preprocessInput(payload));
            response = handleResponse(lambdaClient.invoke(request));
        } catch (JsonProcessingException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new ProcessingException(ex);
        } catch (ServiceException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new LambdaException(ex);
        }
        return response;
    }

    private InvokeRequest getInvokeRequest(I payload) throws JsonProcessingException {
        String stringifiedPayload = getObjectMapper().writeValueAsString(payload);
        return InvokeRequest.builder()
                .functionName(getName())
                .payload(SdkBytes.fromUtf8String(stringifiedPayload))
                .build();
    }

    protected abstract O handleResponse(InvokeResponse invokeResponse) throws JsonProcessingException;
}
