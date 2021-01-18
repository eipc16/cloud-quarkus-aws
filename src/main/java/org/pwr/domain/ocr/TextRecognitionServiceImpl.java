package org.pwr.domain.ocr;

import org.pwr.domain.buckets.FileDetails;
import org.pwr.domain.ocr.tesseract.TesseractLambdaFunction;
import org.pwr.domain.ocr.tesseract.TesseractResponse;
import org.pwr.domain.settings.SettingsService;
import org.pwr.infrastructure.exceptions.TesseractFunctionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Transactional
@Dependent
@Default
public class TextRecognitionServiceImpl implements TextRecognitionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextRecognitionServiceImpl.class);

    private TesseractLambdaFunction tesseractLambdaFunction;
    private SettingsService settingsService;

    @Inject
    public TextRecognitionServiceImpl(TesseractLambdaFunction tesseractLambdaFunction, SettingsService settingsService) {
        this.tesseractLambdaFunction = tesseractLambdaFunction;
        this.settingsService = settingsService;
    }

    public TextRecognitionResult performTextRecognition(FileDetails document) {
        TextRecognitionResult response;
        try {
            TesseractResponse tesseractResponse = tesseractLambdaFunction.apply(document);
            double confidenceThreshold = settingsService.getSettings().getOcrInsufficientConfidenceThreshold();
            if(tesseractResponse.getConfidence() < confidenceThreshold) {
                response = buildInsufficientConfidenceResult(tesseractResponse, LocalDateTime.now());
            } else {
                response = buildSuccessfulResult(tesseractResponse, LocalDateTime.now());
            }
        } catch (TesseractFunctionException ex) {
            LOGGER.warn(ex.getMessage(), ex);
            response = buildFailureResult(LocalDateTime.now());
        }

        if (response == null) {
            throw new RuntimeException("Response shouldn't be null at this point!");
        }

        return response;
    }

    private TextRecognitionResult buildFailureResult(LocalDateTime processedAt) {
        return TextRecognitionResult.builder(TextRecognitionResult.ResultType.FAILURE)
                .withOCRProcessedAt(processedAt)
                .build();
    }

    private TextRecognitionResult buildInsufficientConfidenceResult(TesseractResponse response, LocalDateTime processedAt) {
        return TextRecognitionResult.builder(TextRecognitionResult.ResultType.INSUFFICIENT_CONFIDENCE)
                .withOCRProcessedAt(processedAt)
                .withConfidence(response.getConfidence())
                .withResult(response.getResult())
                .build();
    }

    private TextRecognitionResult buildSuccessfulResult(TesseractResponse response, LocalDateTime processedAt) {
        return TextRecognitionResult.builder(TextRecognitionResult.ResultType.SUCCESS)
                .withConfidence(response.getConfidence())
                .withOCRProcessedAt(processedAt)
                .withResult(response.getResult())
                .build();
    }
}
