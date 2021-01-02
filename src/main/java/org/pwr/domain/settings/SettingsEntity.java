package org.pwr.domain.settings;

public class SettingsEntity {

    private double ocrInsufficientConfidenceThreshold;

    private SettingsEntity(Builder builder) {
        this.ocrInsufficientConfidenceThreshold = builder.ocrInsufficientConfidenceThreshold;
    }

    public double getOcrInsufficientConfidenceThreshold() {
        return ocrInsufficientConfidenceThreshold;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SettingsEntity clone(SettingsEntity settingsEntity) {
        return new Builder(settingsEntity).build();
    }

    public static class Builder {
        private double ocrInsufficientConfidenceThreshold;

        private Builder() {

        }

        private Builder(SettingsEntity settingsEntity) {
            ocrInsufficientConfidenceThreshold = settingsEntity.ocrInsufficientConfidenceThreshold;
        }

        public Builder withOCRInsufficientConfidenceThreshold(double ocrInsufficientConfidenceThreshold) {
            this.ocrInsufficientConfidenceThreshold = ocrInsufficientConfidenceThreshold;
            return this;
        }

        public SettingsEntity build() {
            return new SettingsEntity(this);
        }
    }
}
