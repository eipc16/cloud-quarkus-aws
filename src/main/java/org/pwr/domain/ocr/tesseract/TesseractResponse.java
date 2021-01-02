package org.pwr.domain.ocr.tesseract;

public class TesseractResponse {

    private double confidence;
    private String result;

    private TesseractResponse() {
        // empty for deserialization
    }

    public double getConfidence() {
        return confidence;
    }

    public String getResult() {
        return result;
    }
}
