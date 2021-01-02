package org.pwr.domain.documents;

import org.pwr.domain.buckets.BucketServiceImpl;
import org.pwr.domain.buckets.FileDetails;
import org.pwr.domain.ocr.TextRecognitionResult;
import org.pwr.domain.ocr.TextRecognitionService;
import org.pwr.domain.translation.TranslationResult;
import org.pwr.domain.translation.TranslationService;
import org.pwr.domain.translation.translate.TranslateRequest;
import org.pwr.infrastructure.config.DocumentsConfiguration;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Dependent
public class DocumentsService {

    private final DocumentsConfiguration configuration;
    private final BucketServiceImpl bucketService;
    private final TextRecognitionService textRecognitionService;
    private final TranslationService translationService;

    @Inject
    DocumentsService(DocumentsConfiguration configuration,
                     BucketServiceImpl bucketService,
                     TextRecognitionService textRecognitionService,
                     TranslationService translationService) {
        this.configuration = configuration;
        this.bucketService = bucketService;
        this.textRecognitionService = textRecognitionService;
        this.translationService = translationService;
    }

    public Map<String, Object> processDocument(DocumentData documentData) {
        Map<String, Object> result = new HashMap<>();

        String originalFileName = documentData.fileName;
        String randomName = UUID.randomUUID().toString();
        documentData.fileName = randomName;

        FileDetails fileDetails = bucketService.uploadFile(configuration.getBucket(), documentData);

        result.put("originalName", originalFileName);
        result.put("fileDetails", fileDetails);

        TextRecognitionResult textRecognitionResult = textRecognitionService.performTextRecognition(fileDetails);

        result.put("textRecognitionResult", textRecognitionResult);

        if(textRecognitionResult.getResultType().equals(TextRecognitionResult.ResultType.SUCCESS) && textRecognitionResult.getResult().isPresent()) {
            TranslateRequest translateRequest = TranslateRequest.builder()
                    .withText(textRecognitionResult.getResult().get())
                    .withSourceLanguage(documentData.getSourceLanguage())
                    .withTargetLanguage(documentData.getTargetLanguage())
                    .build();
            TranslationResult translationResult = translationService.performTranslation(translateRequest);
            result.put("translationResult", translationResult);
        }
        return result;
    }
}
