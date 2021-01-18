package org.pwr.domain.translation;

import org.pwr.domain.translation.translate.TranslateRequest;

public interface TranslationService {
    public TranslationResult performTranslation(TranslateRequest request);
}
