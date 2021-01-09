package org.pwr.domain.translation;

import java.time.LocalDateTime;
import java.util.Optional;

public class TranslationResult {

    private String translatedText;
    private double confidence;
    private String sourceLanguage;
    private String targetLanguage;
    private ResultType resultType;
    private LocalDateTime translatedAt;

    private TranslationResult(Builder builder) {
        translatedText = builder.translatedText;
        confidence = Optional.ofNullable(builder.confidence)
                .orElse(0.0);
        sourceLanguage = builder.sourceLanguage;
        targetLanguage = builder.targetLanguage;
        resultType = builder.resultType;
        translatedAt = builder.translatedAt;
    }

    public Optional<String> getTranslatedText() {
        return Optional.ofNullable(translatedText);
    }

    public double getConfidence() {
        return confidence;
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

    public Optional<LocalDateTime> getTranslatedAt() {
        return Optional.ofNullable(translatedAt);
    }

    public static Builder builder(ResultType resultType) {
        return new Builder(resultType);
    }

    public static class Builder {
        private String translatedText;
        private Double confidence;
        private String sourceLanguage;
        private String targetLanguage;
        private ResultType resultType;
        private LocalDateTime translatedAt;

        Builder(ResultType resultType) {
            this.resultType = resultType;
        }

        public Builder withTranslatedText(String translatedText) {
            this.translatedText = translatedText;
            return this;
        }

        public Builder withConfidence(Double confidence) {
            this.confidence = confidence;
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

        public Builder withTranslatedAt(LocalDateTime translatedAt) {
            this.translatedAt = translatedAt;
            return this;
        }

        public TranslationResult build() {
            return new TranslationResult(this);
        }
    }

    public enum ResultType {
        SUCCESS,
        MANUAL,
        FAILURE,
        INSUFFICIENT_CONFIDENCE,
        NOT_STARTED
    }
}
