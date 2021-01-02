package org.pwr.domain.ocr;

import java.util.Optional;

public class TextRecognitionResult {

    private final double confidence;
    private final ResultType resultType;
    private final String result;

    private TextRecognitionResult(Builder builder) {
        this.confidence = builder.confidence;
        this.resultType = builder.resultType;
        this.result = builder.result;
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

    public static Builder builder(ResultType resultType) {
        return new Builder(resultType);
    }

    public static class Builder {
        private double confidence;
        private ResultType resultType;
        private String result;

        private Builder(ResultType resultType) {
            this.resultType = resultType;
        }

        public Builder withConfidence(double confidence) {
            this.confidence = confidence;
            return this;
        }

        public Builder withResult(String result) {
            this.result = result;
            return this;
        }

        public TextRecognitionResult build() {
            return new TextRecognitionResult(this);
        }
    }

    public enum ResultType {
        SUCCESS,
        FAILURE,
        INSUFFICIENT_CONFIDENCE
    }
}
