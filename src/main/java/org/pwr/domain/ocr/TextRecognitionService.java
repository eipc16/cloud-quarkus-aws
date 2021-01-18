package org.pwr.domain.ocr;

import org.pwr.domain.buckets.FileDetails;
import org.pwr.domain.ocr.tesseract.TesseractResponse;

import java.time.LocalDateTime;

public interface TextRecognitionService {
    public TextRecognitionResult performTextRecognition(FileDetails document);
}
