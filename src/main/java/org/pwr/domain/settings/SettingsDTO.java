package org.pwr.domain.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public class SettingsDTO {

    @JsonProperty("ocrThreshold")
    private Double ocrThreshold;

    @JsonProperty("translationThreshold")
    private Double translationThreshold;

    public SettingsDTO() {
        // empty
    }

    public SettingsDTO(Builder builder) {
        ocrThreshold = builder.ocrThreshold;
        translationThreshold = builder.translationThreshold;
    }

    @JsonIgnore
    public Double getOCRTreshold() {
        return Optional.ofNullable(ocrThreshold)
                .orElseThrow(() -> new RuntimeException("ocrThreshold cannot be null!"));
    }

    @JsonIgnore
    public Double getTranslationThreshold() {
        return Optional.ofNullable(translationThreshold)
                .orElseThrow(() -> new RuntimeException("translationThreshold cannot be null!"));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Double ocrThreshold;
        private Double translationThreshold;

        private Builder() {
            // empty
        }

        Builder withOCRThreshold(double ocrThreshold) {
            this.ocrThreshold = ocrThreshold;
            return this;
        }

        Builder withTranslationThreshold(double translationThreshold) {
            this.translationThreshold = translationThreshold;
            return this;
        }

        public SettingsDTO build() {
            return new SettingsDTO(this);
        }
    }
}
