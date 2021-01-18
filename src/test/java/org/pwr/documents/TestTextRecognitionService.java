package org.pwr.documents;

import org.pwr.domain.buckets.FileDetails;
import org.pwr.domain.ocr.TextRecognitionResult;
import org.pwr.domain.ocr.TextRecognitionService;
import org.pwr.infrastructure.qualifiers.TestBean;

import java.time.LocalDateTime;

@TestBean
public class TestTextRecognitionService implements TextRecognitionService {
    @Override
    public TextRecognitionResult performTextRecognition(FileDetails document) {
        return TextRecognitionResult.builder(TextRecognitionResult.ResultType.SUCCESS)
                .withConfidence((double) 100)
                .withOCRProcessedAt(LocalDateTime.now())
                .withResult("success")
                .build();
    }
}
