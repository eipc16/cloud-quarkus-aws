package org.pwr.domain.translation;

import org.pwr.domain.translation.translate.TranslateLambdaFunction;
import org.pwr.domain.translation.translate.TranslateRequest;
import org.pwr.domain.translation.translate.TranslateResponse;
import org.pwr.infrastructure.config.TesseractConfiguration;
import org.pwr.infrastructure.exceptions.TesseractFunctionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.transaction.Transactional;

@Transactional
@Dependent
public class TranslationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TranslationService.class);

    private TranslateLambdaFunction translateLambdaFunction;
    private TesseractConfiguration configuration;

    @Inject
    public TranslationService(TranslateLambdaFunction translateLambdaFunction, TesseractConfiguration configuration) {
        this.translateLambdaFunction = translateLambdaFunction;
        this.configuration = configuration;
    }

    public TranslationResult performTranslation(TranslateRequest request) {
        TranslationResult response;
        try {
            TranslateResponse translateResponse = translateLambdaFunction.apply(request);
            response = buildSuccessfulResult(translateResponse);
        } catch (TesseractFunctionException ex) {
            LOGGER.warn(ex.getMessage(), ex);
            response = buildFailureResult();
        }

        if (response == null) {
            throw new RuntimeException("Response shouldn't be null at this point!");
        }

        return response;
    }

    private TranslationResult buildFailureResult() {
        return TranslationResult.builder(TranslationResult.ResultType.FAILURE)
                .build();
    }

    private TranslationResult buildSuccessfulResult(TranslateResponse response) {
        return TranslationResult.builder(TranslationResult.ResultType.SUCCESS)
                .withTranslatedText(response.getResult())
                .withSourceLanguage(response.getSourceLanguage())
                .withTargetLanguage(response.getTargetLanguage())
                .build();
    }
}
