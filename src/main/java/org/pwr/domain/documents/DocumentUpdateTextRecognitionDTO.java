package org.pwr.domain.documents;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public class DocumentUpdateTextRecognitionDTO {

    @JsonProperty("text")
    private String text;

    public DocumentUpdateTextRecognitionDTO() {
        // empty
    }

    public String getText() {
        return Optional.ofNullable(text)
                .orElseThrow(() -> new RuntimeException("Property 'text' is required!"));
    }
}
