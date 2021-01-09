package org.pwr.domain.translation.translate;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TranslateResponse {

    @JsonProperty("result")
    private String text;

    @JsonProperty("confidence")
    private double confidence;

    @JsonProperty("sourceLang")
    private String sourceLang;

    @JsonProperty("targetLang")
    private String targetLang;

    private TranslateResponse() {
        // empty for deserialization
    }

    public String getResult() {
        return text;
    }

    public double getConfidence() {
        return confidence;
    }

    public String getSourceLanguage() {
        return sourceLang;
    }

    public String getTargetLanguage() {
        return targetLang;
    }
}
