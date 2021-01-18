package org.pwr.domain.translation;

import org.pwr.domain.settings.SettingsService;
import org.pwr.domain.translation.translate.TranslateLambdaFunction;
import org.pwr.domain.translation.translate.TranslateRequest;
import org.pwr.domain.translation.translate.TranslateResponse;
import org.pwr.infrastructure.exceptions.TesseractFunctionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Transactional
@Dependent
public class TranslationServiceImpl implements TranslationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TranslationServiceImpl.class);

    private TranslateLambdaFunction translateLambdaFunction;
    private SettingsService settingsService;

    @Inject
    public TranslationServiceImpl(TranslateLambdaFunction translateLambdaFunction, SettingsService settingsService) {
        this.translateLambdaFunction = translateLambdaFunction;
        this.settingsService = settingsService;
    }

    public TranslationResult performTranslation(TranslateRequest request) {
        TranslationResult response;
        try {
            TranslateResponse translateResponse = translateLambdaFunction.apply(request);
            double confidenceTreshold = settingsService.getSettings().getTranslateInsufficientConfidenceThreshold();
            if (translateResponse.getConfidence() < confidenceTreshold) {
                response = buildInsufficientConfidenceResult(translateResponse, LocalDateTime.now());
            } else {
                response = buildSuccessfulResult(translateResponse, LocalDateTime.now());
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

    private TranslationResult buildFailureResult(LocalDateTime processedAt) {
        return TranslationResult.builder(TranslationResult.ResultType.FAILURE)
                .withTranslatedAt(processedAt)
                .build();
    }

    private TranslationResult buildInsufficientConfidenceResult(TranslateResponse response, LocalDateTime processedAt) {
        return TranslationResult.builder(TranslationResult.ResultType.INSUFFICIENT_CONFIDENCE)
                .withTranslatedAt(processedAt)
                .withTranslatedText(response.getResult())
                .withConfidence(response.getConfidence())
                .build();
    }

    private TranslationResult buildSuccessfulResult(TranslateResponse response, LocalDateTime processedAt) {
        return TranslationResult.builder(TranslationResult.ResultType.SUCCESS)
                .withTranslatedText(response.getResult())
                .withTranslatedAt(processedAt)
                .withConfidence(response.getConfidence())
                .withSourceLanguage(response.getSourceLanguage())
                .withTargetLanguage(response.getTargetLanguage())
                .build();
    }
}
