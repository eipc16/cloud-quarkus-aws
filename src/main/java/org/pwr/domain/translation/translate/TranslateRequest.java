package org.pwr.domain.translation.translate;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public class TranslateRequest {

    @JsonProperty("text")
    private String text;

    @JsonProperty("sourceLang")
    private String sourceLang;

    @JsonProperty("targetLang")
    private String targetLang;

    private TranslateRequest(Builder builder) {
        text = Optional.ofNullable(builder.text).orElse("");
        sourceLang = Optional.ofNullable(builder.sourceLang).orElse("en");
        targetLang = Optional.ofNullable(builder.targetLang).orElse("pl");
    }

    public String getText() {
        return text;
    }

    public String getSourceLanguage() {
        return sourceLang;
    }

    public String getTargetLanguage() {
        return targetLang;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String text;
        private String sourceLang;
        private String targetLang;

        public Builder withText(String text) {
            this.text = text;
            return this;
        }

        public Builder withSourceLanguage(String sourceLanguage) {
            this.sourceLang = sourceLanguage;
            return this;
        }

        public Builder withTargetLanguage(String targetLanguage) {
            this.targetLang = targetLanguage;
            return this;
        }

        public TranslateRequest build() {
            return new TranslateRequest(this);
        }
    }
}
