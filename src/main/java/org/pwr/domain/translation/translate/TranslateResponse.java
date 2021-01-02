package org.pwr.domain.translation.translate;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TranslateResponse {

    @JsonProperty("text")
    private String text;

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

    public String getSourceLanguage() {
        return sourceLang;
    }

    public String getTargetLanguage() {
        return targetLang;
    }
}
