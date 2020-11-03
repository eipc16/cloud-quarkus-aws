package org.pwr.domain.buckets;

import software.amazon.awssdk.services.rekognition.model.Label;

public class LabelDetail {
    private double confidence;
    private String detectedText;

    public LabelDetail() {

    }

    public LabelDetail(Label label) {
        confidence = label.confidence();
        detectedText = label.name();
    }

    public double getConfidence() {
        return confidence;
    }

    public String getDetectedText() {
        return detectedText;
    }
}
