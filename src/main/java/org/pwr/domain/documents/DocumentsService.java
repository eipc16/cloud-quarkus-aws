package org.pwr.domain.documents;

import org.pwr.domain.buckets.BucketServiceImpl;
import org.pwr.domain.buckets.FileDetails;
import org.pwr.domain.buckets.FileDownloadInformation;
import org.pwr.domain.buckets.StreamingResponse;
import org.pwr.domain.ocr.TextRecognitionResult;
import org.pwr.domain.ocr.TextRecognitionService;
import org.pwr.domain.translation.TranslationResult;
import org.pwr.domain.translation.TranslationService;
import org.pwr.domain.translation.translate.TranslateRequest;
import org.pwr.infrastructure.config.DocumentsConfiguration;
import org.pwr.infrastructure.config.TranslateConfiguration;
import org.pwr.infrastructure.dynamodb.DynamoPage;
import org.pwr.infrastructure.dynamodb.DynamoPaginable;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Dependent
public class DocumentsService {

    private final DocumentsConfiguration configuration;
    private final BucketServiceImpl bucketService;
    private final TextRecognitionService textRecognitionService;
    private final TranslationService translationService;
    private final DocumentsRepository documentsRepository;

    @Inject
    DocumentsService(DocumentsConfiguration configuration,
                     BucketServiceImpl bucketService,
                     TextRecognitionService textRecognitionService,
                     TranslationService translationService,
                     DocumentsRepository documentsRepository) {
        this.configuration = configuration;
        this.bucketService = bucketService;
        this.textRecognitionService = textRecognitionService;
        this.translationService = translationService;
        this.documentsRepository = documentsRepository;
    }

    public DocumentEntity processDocument(DocumentData documentData, String userName) {
        String key = getRandomDocumentKey(userName);
        DocumentEntity documentEntity = bucketService.uploadFileAndThen(configuration.getBucket(), key, documentData, processUploadedFile(documentData, userName));
        return documentEntity;
    }

    private String getRandomDocumentKey(String userName) {
        return getRandomObjectKey(
                () -> userName,
                () -> DateTimeFormatter.ofPattern("uuuu-MM-dd").format(LocalDate.now()),
                () -> UUID.randomUUID().toString()
        );
    }

    @SafeVarargs
    private String getRandomObjectKey(Supplier<String>... keyPartSuppliers) {
        if (keyPartSuppliers.length < 1) {
            throw new RuntimeException("At least one key part is required!");
        }
        return Stream.of(keyPartSuppliers)
                .map(Supplier::get)
                .collect(Collectors.joining("/"));
    }

    private Function<FileDetails, DocumentEntity> processUploadedFile(DocumentData documentData, String user) {
        return fileDetails -> processUploadedFile(documentData, fileDetails, user);
    }

    private DocumentEntity processUploadedFile(DocumentData documentData, FileDetails fileDetails, String user) {
        TextRecognitionResult textRecognitionResult = performTextRecognition(fileDetails);
        DocumentEntity entity = buildEntityBeforeTranslation(fileDetails, textRecognitionResult, user);
        if (textRecognitionResult.getResultType().equals(TextRecognitionResult.ResultType.SUCCESS) && textRecognitionResult.getResult().isPresent()) {
            TranslationResult translationResult = performTextTranslation(documentData, textRecognitionResult);
            entity = DocumentEntity.builder(entity)
                    .withTranslationResult(translationResult)
                    .build();
        }
        return documentsRepository.saveDocumentData(entity);
    }

    private DocumentEntity buildEntityBeforeTranslation(FileDetails fileDetails, TextRecognitionResult textRecognitionResult, String user) {
        return DocumentEntity.builder(fileDetails, user, LocalDateTime.now())
                .withTextRecognitionResult(textRecognitionResult)
                .withTranslationResult(TranslationResult.builder(TranslationResult.ResultType.NOT_STARTED)
                        .build())
                .build();
    }

    private TextRecognitionResult performTextRecognition(FileDetails fileDetails) {
        return textRecognitionService.performTextRecognition(fileDetails);
    }

    private TranslationResult performTextTranslation(DocumentData documentData, TextRecognitionResult textRecognitionResult) {
        return performTextTranslation(documentData.getSourceLanguage(), documentData.getTargetLanguage(), textRecognitionResult);
    }

    private TranslationResult performTextTranslation(String sourceLanguage, String targetLanguage, TextRecognitionResult textRecognitionResult) {
        if (textRecognitionResult.getResult().isEmpty()) {
            throw new RuntimeException("Cannot translate not recognized text");
        }
        TranslateRequest translateRequest = TranslateRequest.builder()
                .withText(textRecognitionResult.getResult().get())
                .withSourceLanguage(sourceLanguage)
                .withTargetLanguage(targetLanguage)
                .build();
        return translationService.performTranslation(translateRequest);
    }

    public DynamoPage<DocumentEntity> getDocuments(DynamoPaginable dynamoPaginable, DocumentSearchFilter documentSearchFilter) {
        return documentsRepository.getDocuments(dynamoPaginable, documentSearchFilter);
    }

    public DocumentEntity getDocumentById(String documentId) {
        return documentsRepository.getDocumentById(documentId);
    }

    public FileDownloadInformation downloadDocument(String documentId) {
        DocumentEntity documentEntity = getDocumentById(documentId);
        StreamingResponse output = bucketService.downloadFile(documentEntity.getFileDetails());
        return new FileDownloadInformation(documentEntity.getFileDetails(), output);
    }

    public DocumentEntity updateTextRecognition(String documentId, String newTextRecognition,
                                                String userName) {
        DocumentEntity documentEntity = getDocumentById(documentId);
        LocalDateTime currentTime = LocalDateTime.now();
        TextRecognitionResult manualTextRecognitonResult = buildManualTextRecognitionResult(newTextRecognition, currentTime);
        DocumentEntity updatedEntity = buildManualTextRecognitionDocumentEntity(documentEntity, manualTextRecognitonResult, userName, currentTime);

        String sourceLanguage = updatedEntity.getTranslationResult()
                .flatMap(TranslationResult::getSourceLanguage)
                .orElse(null);
        String targetLanguage = updatedEntity.getTranslationResult()
                .flatMap(TranslationResult::getTargetLanguage)
                .orElse(null);

        TranslationResult translationResult = performTextTranslation(sourceLanguage, targetLanguage, manualTextRecognitonResult);

        DocumentEntity fullyUpdatedEntity = DocumentEntity.builder(updatedEntity)
                .withTranslationResult(translationResult)
                .build();

        return documentsRepository.saveDocumentData(fullyUpdatedEntity);
    }

    private TextRecognitionResult buildManualTextRecognitionResult(String newTextRecognition, LocalDateTime currentTime) {
        return TextRecognitionResult.builder(TextRecognitionResult.ResultType.MANUAL)
                .withOCRProcessedAt(currentTime)
                .withResult(newTextRecognition)
                .withConfidence(100.0)
                .build();
    }

    private DocumentEntity buildManualTextRecognitionDocumentEntity(DocumentEntity documentEntity, TextRecognitionResult textRecognitionResult,
                                                                    String userName, LocalDateTime currentTime) {
        return DocumentEntity.builder(documentEntity)
                .withTextRecognitionResult(textRecognitionResult)
                .withModifiedBy(userName)
                .withModifiedAt(currentTime)
                .build();
    }

    public DocumentEntity updateTranslation(String documentId, String newTranslation,
                                            String sourceLanguage, String targetLanguage,
                                            String userName) {
        DocumentEntity documentEntity = getDocumentById(documentId);
        LocalDateTime currentTime = LocalDateTime.now();
        DocumentEntity updatedEntity = buildManualTranslationEntity(documentEntity, newTranslation, sourceLanguage, targetLanguage, userName, currentTime);
        return documentsRepository.saveDocumentData(updatedEntity);
    }

    private DocumentEntity buildManualTranslationEntity(DocumentEntity documentEntity, String newTranslation,
                                                        String sourceLanguage, String targetLanguage,
                                                        String userName, LocalDateTime currentTime) {
        return DocumentEntity.builder(documentEntity)
                .withTranslationResult(TranslationResult.builder(TranslationResult.ResultType.MANUAL)
                        .withTranslatedAt(currentTime)
                        .withTranslatedText(newTranslation)
                        .withSourceLanguage(sourceLanguage)
                        .withTargetLanguage(targetLanguage)
                        .withConfidence(100.0)
                        .build())
                .withModifiedAt(currentTime)
                .withModifiedBy(userName)
                .build();
    }
}
