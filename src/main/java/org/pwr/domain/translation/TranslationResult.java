package org.pwr.domain.translation;

import org.pwr.domain.buckets.FileDetails;

import java.util.Optional;

public class TranslationResult {

    private String translatedText;
    private String sourceLanguage;
    private String targetLanguage;
    private ResultType resultType;

    private TranslationResult(Builder builder) {
        translatedText = builder.translatedText;
        sourceLanguage = builder.sourceLanguage;
        targetLanguage = builder.targetLanguage;
        resultType = builder.resultType;
    }

    public Optional<String> getTranslatedText() {
        return Optional.ofNullable(translatedText);
    }

    public Optional<String> getSourceLanguage() {
        return Optional.ofNullable(sourceLanguage);
    }

    public Optional<String> getTargetLanguage() {
        return Optional.ofNullable(targetLanguage);
    }

    public ResultType getResultType() {
        return resultType;
    }

    public static Builder builder(ResultType resultType) {
        return new Builder(resultType);
    }

    static class Builder {
        private String translatedText;
        private String sourceLanguage;
        private String targetLanguage;
        private ResultType resultType;

        Builder(ResultType resultType) {
            this.resultType = resultType;
        }

        public Builder withTranslatedText(String translatedText) {
            this.translatedText = translatedText;
            return this;
        }

        public Builder withSourceLanguage(String sourceLanguage) {
            this.sourceLanguage = sourceLanguage;
            return this;
        }

        public Builder withTargetLanguage(String targetLanguage) {
            this.targetLanguage = targetLanguage;
            return this;
        }

        public TranslationResult build() {
            return new TranslationResult(this);
        }
    }

    public enum ResultType {
        SUCCESS,
        FAILURE,
        INSUFFICIENT_CONFIDENCE
    }
}
