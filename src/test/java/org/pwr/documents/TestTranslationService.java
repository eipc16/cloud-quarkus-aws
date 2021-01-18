package org.pwr.documents;

import org.pwr.domain.translation.TranslationResult;
import org.pwr.domain.translation.TranslationService;
import org.pwr.domain.translation.translate.TranslateRequest;

import java.time.LocalDateTime;

public class TestTranslationService implements TranslationService {
    @Override
    public TranslationResult performTranslation(TranslateRequest request) {
        return TranslationResult.builder(TranslationResult.ResultType.SUCCESS)
                .withTranslatedText("Translated text")
                .withTranslatedAt(LocalDateTime.now())
                .withConfidence((double) 100)
                .withSourceLanguage("en")
                .withTargetLanguage("pl")
                .build();
    }
}
