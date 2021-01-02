package org.pwr.domain.ocr;

import org.pwr.domain.buckets.FileDetails;
import org.pwr.domain.ocr.tesseract.TesseractLambdaFunction;
import org.pwr.domain.ocr.tesseract.TesseractResponse;
import org.pwr.domain.settings.SettingsService;
import org.pwr.infrastructure.exceptions.TesseractFunctionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.transaction.Transactional;

@Transactional
@Dependent
public class TextRecognitionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextRecognitionService.class);

    private TesseractLambdaFunction tesseractLambdaFunction;
    private SettingsService settingsService;

    @Inject
    public TextRecognitionService(TesseractLambdaFunction tesseractLambdaFunction, SettingsService settingsService) {
        this.tesseractLambdaFunction = tesseractLambdaFunction;
        this.settingsService = settingsService;
    }

    public TextRecognitionResult performTextRecognition(FileDetails document) {
        TextRecognitionResult response;
        try {
            TesseractResponse tesseractResponse = tesseractLambdaFunction.apply(document);
            double confidenceThreshold = settingsService.getSettings().getOcrInsufficientConfidenceThreshold();
            if(tesseractResponse.getConfidence() < confidenceThreshold) {
                response = buildInsufficientConfidenceResult(tesseractResponse);
            } else {
                response = buildSuccessfulResult(tesseractResponse);
            }
        } catch (TesseractFunctionException ex) {
            LOGGER.warn(ex.getMessage(), ex);
            response = buildFailureResult();
        }

        if (response == null) {
            throw new RuntimeException("Response shouldn't be null at this point!");
        }

        return response;
    }

    private TextRecognitionResult buildFailureResult() {
        return TextRecognitionResult.builder(TextRecognitionResult.ResultType.FAILURE)
                .build();
    }

    private TextRecognitionResult buildInsufficientConfidenceResult(TesseractResponse response) {
        return TextRecognitionResult.builder(TextRecognitionResult.ResultType.INSUFFICIENT_CONFIDENCE)
                .withConfidence(response.getConfidence())
                .build();
    }

    private TextRecognitionResult buildSuccessfulResult(TesseractResponse response) {
        return TextRecognitionResult.builder(TextRecognitionResult.ResultType.SUCCESS)
                .withConfidence(response.getConfidence())
                .withResult(response.getResult())
                .build();
    }
}
