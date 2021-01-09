package org.pwr.domain.settings;

public class SettingsEntity {

    private final double ocrInsufficientConfidenceThreshold;
    private final double translateInsufficientConfidenceThreshold;

    private SettingsEntity(Builder builder) {
        this.ocrInsufficientConfidenceThreshold = builder.ocrInsufficientConfidenceThreshold;
        this.translateInsufficientConfidenceThreshold = builder.translateInsufficientConfidenceThreshold;
    }

    public double getOcrInsufficientConfidenceThreshold() {
        return ocrInsufficientConfidenceThreshold;
    }

    public double getTranslateInsufficientConfidenceThreshold() {
        return translateInsufficientConfidenceThreshold;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SettingsEntity clone(SettingsEntity settingsEntity) {
        return new Builder(settingsEntity).build();
    }

    public static class Builder {
        private double ocrInsufficientConfidenceThreshold;
        private double translateInsufficientConfidenceThreshold;

        private Builder() {

        }

        private Builder(SettingsEntity settingsEntity) {
            ocrInsufficientConfidenceThreshold = settingsEntity.ocrInsufficientConfidenceThreshold;
            translateInsufficientConfidenceThreshold = settingsEntity.translateInsufficientConfidenceThreshold;
        }

        public Builder withOCRInsufficientConfidenceThreshold(double ocrInsufficientConfidenceThreshold) {
            this.ocrInsufficientConfidenceThreshold = ocrInsufficientConfidenceThreshold;
            return this;
        }

        public Builder withTranslateInsufficientConfidenceThreshold(double translateInsufficientConfidenceThreshold) {
            this.translateInsufficientConfidenceThreshold = translateInsufficientConfidenceThreshold;
            return this;
        }

        public SettingsEntity build() {
            return new SettingsEntity(this);
        }
    }
}
