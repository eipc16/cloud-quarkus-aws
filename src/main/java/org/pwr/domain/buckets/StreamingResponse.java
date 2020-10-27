package org.pwr.domain.buckets;

import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import javax.ws.rs.core.StreamingOutput;

public class StreamingResponse {

    private String contentType;
    private StreamingOutput stream;

    public StreamingResponse(String contentType, StreamingOutput stream) {
        this.contentType = contentType;
        this.stream = stream;
    }

    public String getContentType() {
        return contentType;
    }

    public StreamingOutput getOutput() {
        return stream;
    }
}
