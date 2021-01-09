package org.pwr.domain.documents;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public class DocumentUpdateTranslationDTO {

    @JsonProperty("text")
    private String text;

    @JsonProperty("sourceLanguage")
    private String sourceLanguage;

    @JsonProperty("targetLanguage")
    private String targetLanguage;

    public String getText() {
        return Optional.ofNullable(text)
                .orElseThrow(() -> new RuntimeException("Property 'text' is required"));
    }

    public String getSourceLanguage() {
        return Optional.ofNullable(sourceLanguage)
                .orElseThrow(() -> new RuntimeException("Property 'sourceLanguage' is required!"));
    }

    public String getTargetLanguage() {
        return Optional.ofNullable(targetLanguage)
                .orElseThrow(() -> new RuntimeException("Property 'targetLanguage' is required!"));
    }
}
