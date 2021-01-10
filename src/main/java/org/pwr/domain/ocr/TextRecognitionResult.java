package org.pwr.domain.ocr;

import com.fasterxml.jackson.annotation.JsonGetter;

import java.time.LocalDateTime;
import java.util.Optional;

public class TextRecognitionResult {

    private final double confidence;
    private final ResultType resultType;
    private final String result;
    private final LocalDateTime ocrProcessedAt;

    private TextRecognitionResult(Builder builder) {
        this.confidence = Optional.ofNullable(builder.confidence).orElse(0D);
        this.resultType = builder.resultType;
        this.result = builder.result;
        this.ocrProcessedAt = builder.ocrProcessedAt;
    }

    @JsonGetter("ocrProcessedAt")
    public String getOCRProcessedAtAsString() {
        return Optional.ofNullable(ocrProcessedAt)
                .map(LocalDateTime::toString)
                .orElse(null);
    }

    public double getConfidence() {
        return confidence;
    }

    public ResultType getResultType() {
        return resultType;
    }

    public Optional<String> getResult() {
        return Optional.ofNullable(result);
    }

    public Optional<LocalDateTime> getOCRProcessedAt() {
        return Optional.ofNullable(ocrProcessedAt);
    }

    public static Builder builder(ResultType resultType) {
        return new Builder(resultType);
    }

    public static class Builder {
        private Double confidence;
        private ResultType resultType;
        private String result;
        private LocalDateTime ocrProcessedAt;

        private Builder(ResultType resultType) {
            this.resultType = resultType;
        }

        public Builder withConfidence(Double confidence) {
            this.confidence = confidence;
            return this;
        }

        public Builder withResult(String result) {
            this.result = result;
            return this;
        }

        public Builder withOCRProcessedAt(LocalDateTime ocrProcessedAt) {
            this.ocrProcessedAt = ocrProcessedAt;
            return this;
        }

        public TextRecognitionResult build() {
            return new TextRecognitionResult(this);
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
