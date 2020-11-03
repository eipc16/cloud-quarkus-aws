package org.pwr.domain.buckets;

import software.amazon.awssdk.services.rekognition.model.TextDetection;

public class TextDetectionDetail {
    private double confidence;
    private String detectedText;
    private int id;
    private String type;

    public TextDetectionDetail() {

    }

    public TextDetectionDetail(TextDetection text) {
        confidence = text.confidence();
        detectedText = text.detectedText();
        id = text.id();
        type = text.typeAsString();
    }

    public double getConfidence() {
        return confidence;
    }

    public String getDetectedText() {
        return detectedText;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
