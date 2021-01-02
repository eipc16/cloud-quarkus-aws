package org.pwr.domain.settings;

import org.pwr.infrastructure.dynamodb.DynamoDBTable;

import javax.persistence.Id;
import java.util.Optional;

@DynamoDBTable(name = "settings")
public class SettingsDynamoEntity {

    private static final double DEFAULT_INSUFFICIENT_CONFIDENCE_THRESHOLD = 0.90;

    @Id
    private Long id = 0L;

    private Double ocrInsufficientConfidenceThreshold;

    public SettingsDynamoEntity() {
        // empty for deserialization
    }

    private SettingsDynamoEntity(Builder builder) {
        this.ocrInsufficientConfidenceThreshold = Optional.ofNullable(builder.ocrInsufficientConfidenceThreshold)
                .orElse(DEFAULT_INSUFFICIENT_CONFIDENCE_THRESHOLD);
    }

    public double getOcrInsufficientConfidenceThreshold() {
        return ocrInsufficientConfidenceThreshold;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(SettingsDynamoEntity settingsDynamoEntity) {
        return new Builder(settingsDynamoEntity);
    }

    public static class Builder {
        private double ocrInsufficientConfidenceThreshold;

        private Builder() {

        }

        private Builder(SettingsDynamoEntity settingsDynamoEntity) {
            ocrInsufficientConfidenceThreshold = settingsDynamoEntity.ocrInsufficientConfidenceThreshold;
        }

        public Builder withOCRInsufficientConfidenceThreshold(double ocrInsufficientConfidenceThreshold) {
            this.ocrInsufficientConfidenceThreshold = ocrInsufficientConfidenceThreshold;
            return this;
        }

        public SettingsDynamoEntity build() {
            return new SettingsDynamoEntity(this);
        }
    }
}
