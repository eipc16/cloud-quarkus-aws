package org.pwr.domain.documents;

import org.pwr.domain.ocr.TextRecognitionResult;
import org.pwr.domain.translation.TranslationResult;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DocumentMapper {

    DocumentEntity toEntity(DocumentDynamoEntity documentDynamoEntity) {
        return DocumentEntity.builder(documentDynamoEntity.getFileDetails(), documentDynamoEntity.getUploadedBy(), documentDynamoEntity.getUploadedAt())
                .withId(documentDynamoEntity.getId())
                .withName(documentDynamoEntity.getName())
                .withModifiedBy(documentDynamoEntity.getModifiedBy().orElse(null))
                .withModifiedAt(documentDynamoEntity.getModifiedAt().orElse(null))
                .withTextRecognitionResult(TextRecognitionResult.builder(mapToTextRecognitionResultType(documentDynamoEntity.getOcrStatus()))
                        .withConfidence(documentDynamoEntity.getOCRConfidence().map(x -> x == 100 ? x : x * 100).orElse(null))
                        .withOCRProcessedAt(documentDynamoEntity.getOcrProcessedAt().orElse(null))
                        .withResult(documentDynamoEntity.getOCRResult().orElse(null))
                        .build())
                .withTranslationResult(TranslationResult.builder(mapToTranslationResultType(documentDynamoEntity.getTranslationStatus()))
                        .withConfidence(documentDynamoEntity.getTranslationConfidence().orElse(null))
                        .withTranslatedAt(documentDynamoEntity.getTranslationProcessedAt().orElse(null))
                        .withTargetLanguage(documentDynamoEntity.getTargetLanguage())
                        .withSourceLanguage(documentDynamoEntity.getSourceLanguage())
                        .withTranslatedText(documentDynamoEntity.getTranslationResult().orElse(null))
                        .build())
                .build();
    }

    private TextRecognitionResult.ResultType mapToTextRecognitionResultType(ProcessingStatus processingStatus) {
        switch (processingStatus) {
            case FINISHED:
                return TextRecognitionResult.ResultType.SUCCESS;
            case FAILED:
                return TextRecognitionResult.ResultType.FAILURE;
            case INSUFFICIENT_CONFIDENCE:
                return TextRecognitionResult.ResultType.INSUFFICIENT_CONFIDENCE;
            case MANUAL:
                return TextRecognitionResult.ResultType.MANUAL;
            case NOT_STARTED:
            default:
                return TextRecognitionResult.ResultType.NOT_STARTED;
        }
    }

    private TranslationResult.ResultType mapToTranslationResultType(ProcessingStatus processingStatus) {
        switch (processingStatus) {
            case FINISHED:
                return TranslationResult.ResultType.SUCCESS;
            case FAILED:
                return TranslationResult.ResultType.FAILURE;
            case INSUFFICIENT_CONFIDENCE:
                return TranslationResult.ResultType.INSUFFICIENT_CONFIDENCE;
            case MANUAL:
                return TranslationResult.ResultType.MANUAL;
            case NOT_STARTED:
            default:
                return TranslationResult.ResultType.NOT_STARTED;
        }
    }

    DocumentDynamoEntity toDynamoEntity(DocumentEntity documentEntity) {
        DocumentDynamoEntity.Builder builder = DocumentDynamoEntity.builder(documentEntity.getFileDetails())
                .withName(documentEntity.getName())
                .withId(documentEntity.getId().orElse(null))
                .withUploadedAt(documentEntity.getUploadedAt())
                .withUploadedBy(documentEntity.getUploadedBy())
                .withModifiedAt(documentEntity.getModifiedAt().orElse(null))
                .withModifiedBy(documentEntity.getModifiedBy().orElse(null));

        documentEntity.getTextRecognitionResult().ifPresent(textRecognitionResult -> {
            builder.withOCRConfidence(textRecognitionResult.getConfidence())
                    .wthOCRProcessingStatus(mapToProcessingStatus(textRecognitionResult.getResultType()))
                    .withOCRProcessedAt(textRecognitionResult.getOCRProcessedAt().orElse(null))
                    .withOCRResult(textRecognitionResult.getResult().orElse(null));
        });

        documentEntity.getTranslationResult().ifPresent(translationResult -> {
            builder.withTranslationConfidence(translationResult.getConfidence())
                    .withTranslationProcessingStatus(mapToProcessingStatus(translationResult.getResultType()))
                    .withTranslatedAt(translationResult.getTranslatedAt().orElse(null))
                    .withTranslationResult(translationResult.getTranslatedText().orElse(null))
                    .withSourceLanguage(translationResult.getSourceLanguage().orElse(null))
                    .withTargetLanguage(translationResult.getTargetLanguage().orElse(null));
        });

        return builder.build();
    }

    private ProcessingStatus mapToProcessingStatus(TextRecognitionResult.ResultType textRecognitionResult) {
        switch (textRecognitionResult) {
            case SUCCESS:
                return ProcessingStatus.FINISHED;
            case FAILURE:
                return ProcessingStatus.FAILED;
            case INSUFFICIENT_CONFIDENCE:
                return ProcessingStatus.INSUFFICIENT_CONFIDENCE;
            case MANUAL:
                return ProcessingStatus.MANUAL;
            case NOT_STARTED:
            default:
                return ProcessingStatus.NOT_STARTED;
        }
    }

    private ProcessingStatus mapToProcessingStatus(TranslationResult.ResultType translationResult) {
        switch (translationResult) {
            case SUCCESS:
                return ProcessingStatus.FINISHED;
            case FAILURE:
                return ProcessingStatus.FAILED;
            case INSUFFICIENT_CONFIDENCE:
                return ProcessingStatus.INSUFFICIENT_CONFIDENCE;
            case MANUAL:
                return ProcessingStatus.MANUAL;
            case NOT_STARTED:
            default:
                return ProcessingStatus.NOT_STARTED;
        }
    }

    public DocumentDTO toDTO(DocumentEntity documentEntity) {
        return DocumentDTO.builder(documentEntity.getFileDetails())
                .withId(documentEntity.getId().orElse(null))
                .withModifiedAt(documentEntity.getModifiedAt().orElse(null))
                .withModifiedBy(documentEntity.getModifiedBy().orElse(null))
                .withUploadedAt(documentEntity.getUploadedAt())
                .withUploadedBy(documentEntity.getUploadedBy())
                .withTextRecognitionResult(documentEntity.getTextRecognitionResult()
                        .map(TextRecognitionResult::getResultType)
                        .map(this::mapToProcessingStatus).orElse(null))
                .withTextRecognitionConfidence(documentEntity.getTextRecognitionResult().map(TextRecognitionResult::getConfidence).orElse(0d))
                .withTranslationResult(documentEntity.getTranslationResult()
                        .map(TranslationResult::getResultType)
                        .map(this::mapToProcessingStatus)
                        .orElse(null))
                .withTranslationConfidence(documentEntity.getTranslationResult().map(TranslationResult::getConfidence).orElse(0d))
                .withName(documentEntity.getName())
                .build();
    }
}
