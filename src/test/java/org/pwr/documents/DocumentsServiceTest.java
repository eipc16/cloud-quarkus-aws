package org.pwr.documents;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.pwr.domain.buckets.BucketsService;
import org.pwr.domain.buckets.FileDetails;
import org.pwr.domain.buckets.FileDownloadInformation;
import org.pwr.domain.buckets.StreamingResponse;
import org.pwr.domain.documents.DocumentData;
import org.pwr.domain.documents.DocumentEntity;
import org.pwr.domain.documents.DocumentSearchFilter;
import org.pwr.domain.documents.DocumentsRepository;
import org.pwr.domain.ocr.TextRecognitionResult;
import org.pwr.domain.ocr.TextRecognitionService;
import org.pwr.domain.translation.TranslationResult;
import org.pwr.domain.translation.TranslationService;
import org.pwr.domain.translation.translate.TranslateRequest;
import org.pwr.infrastructure.dynamodb.DynamoPage;
import org.pwr.infrastructure.dynamodb.DynamoPaginable;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


@QuarkusTest
public class DocumentsServiceTest {

    @Test
    public void testProcessDocument() {
        BucketsService bucketService = new TestBucketServiceImpl();
        DocumentData data = new DocumentData();
        DocumentEntity expected = processUploadedFile(data, new FileDetails(), "user");

        DocumentEntity documentEntity = bucketService.uploadFileAndThen("bucket", "key", data, processUploadedFile(data, "user"));

        Assertions.assertEquals(expected.getId(), documentEntity.getId());
        Assertions.assertEquals(expected.getTextRecognitionResult().flatMap(TextRecognitionResult::getResult), documentEntity.getTextRecognitionResult().flatMap(TextRecognitionResult::getResult));
        Assertions.assertEquals(expected.getTranslationResult().flatMap(TranslationResult::getTranslatedText), documentEntity.getTranslationResult().flatMap(TranslationResult::getTranslatedText));
    }

    private Function<FileDetails, DocumentEntity> processUploadedFile(DocumentData documentData, String user) {
        return fileDetails -> processUploadedFile(documentData, fileDetails, user);
    }

    private DocumentEntity processUploadedFile(DocumentData documentData, FileDetails fileDetails, String user) {
        TextRecognitionResult textRecognitionResult = performTextRecognition(fileDetails);
        DocumentEntity entity = buildEntityBeforeTranslation(fileDetails, documentData.getName(), textRecognitionResult, user);
        if (textRecognitionResult.getResultType().equals(TextRecognitionResult.ResultType.SUCCESS) && textRecognitionResult.getResult().isPresent()) {
            TranslationResult translationResult = performTextTranslation(documentData, textRecognitionResult);
            entity = DocumentEntity.builder(entity)
                    .withTranslationResult(translationResult)
                    .build();
        }
        return entity;
    }

    private TextRecognitionResult performTextRecognition(FileDetails fileDetails) {
        TextRecognitionService textRecognitionService = new TestTextRecognitionService();
        return textRecognitionService.performTextRecognition(fileDetails);
    }

    private TranslationResult performTextTranslation(DocumentData documentData, TextRecognitionResult textRecognitionResult) {
        return performTextTranslation(documentData.getSourceLanguage(), documentData.getTargetLanguage(), textRecognitionResult);
    }

    private TranslationResult performTextTranslation(String sourceLanguage, String targetLanguage, TextRecognitionResult textRecognitionResult) {
        TranslationService translationService = new TestTranslationService();

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

    private DocumentEntity buildEntityBeforeTranslation(FileDetails fileDetails, String name, TextRecognitionResult textRecognitionResult, String user) {
        return DocumentEntity.builder(fileDetails, user, LocalDateTime.now())
                .withName(name)
                .withTextRecognitionResult(textRecognitionResult)
                .withTranslationResult(TranslationResult.builder(TranslationResult.ResultType.NOT_STARTED)
                        .build())
                .build();
    }

    @Test
    public void testGetDocuments() {
        DocumentsRepository documentsRepository = new TestDocumentRepository();
        DynamoPaginable dynamoPaginable = new DynamoPaginable();
        DocumentSearchFilter documentSearchFilter = new DocumentSearchFilter();
        DocumentEntity documentEntity = DocumentEntity.builder(new FileDetails(), "test", LocalDateTime.now()).build();
        List<DocumentEntity> list = new ArrayList<>();
        list.add(documentEntity);
        Map<String, AttributeValue> map = new HashMap<>();
        DynamoPage<DocumentEntity> page = new DynamoPage<>(list, 15, map, map);

        DynamoPage<DocumentEntity> dynamoPage = documentsRepository.getDocuments(dynamoPaginable, documentSearchFilter);

        Assertions.assertEquals(page.getContent().get(0).getUploadedBy(), dynamoPage.getContent().get(0).getUploadedBy());
    }

    @Test
    public void testGetDocumentById() {
        DocumentsRepository documentsRepository = new TestDocumentRepository();
        DocumentEntity expected = DocumentEntity.builder(new FileDetails(), "test", LocalDateTime.now()).build();

        DocumentEntity documentEntity = documentsRepository.getDocumentById("1");
        Assertions.assertEquals(expected.getUploadedBy(), documentEntity.getUploadedBy());
    }

    @Test
    public void testDownloadDocument() {
        DocumentsRepository documentsRepository = new TestDocumentRepository();
        BucketsService bucketService = new TestBucketServiceImpl();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamingResponse expectedOutput = new StreamingResponse("image/png", baos::writeTo);

        DocumentEntity documentEntity = documentsRepository.getDocumentById("1");
        StreamingResponse output = bucketService.downloadFile(documentEntity.getFileDetails());
        FileDownloadInformation downloadInformation = new FileDownloadInformation(documentEntity.getFileDetails(), output);

        Assertions.assertEquals(documentEntity.getFileDetails(), downloadInformation.getFileDetails());
        Assertions.assertEquals(expectedOutput.getContentType(), output.getContentType());
    }

    @Test
    public void testUpdateTextRecognition() {
        DocumentsRepository documentsRepository = new TestDocumentRepository();
        DocumentEntity documentEntity = documentsRepository.getDocumentById("1");
        String newTextRecognition = "New text recognition";
        LocalDateTime currentTime = LocalDateTime.now();

        TextRecognitionResult textRecognitionResult = TextRecognitionResult.builder(TextRecognitionResult.ResultType.MANUAL)
                .withOCRProcessedAt(currentTime)
                .withResult(newTextRecognition)
                .withConfidence(100.0)
                .build();

        DocumentEntity updatedEntity = DocumentEntity.builder(documentEntity)
                .withTextRecognitionResult(textRecognitionResult)
                .withModifiedBy("User")
                .withModifiedAt(currentTime)
                .build();

        String sourceLanguage = updatedEntity.getTranslationResult()
                .flatMap(TranslationResult::getSourceLanguage)
                .orElse(null);
        String targetLanguage = updatedEntity.getTranslationResult()
                .flatMap(TranslationResult::getTargetLanguage)
                .orElse(null);

        DocumentEntity fullyUpdatedEntity = DocumentEntity.builder(updatedEntity)
                .build();

        DocumentEntity result = documentsRepository.saveDocumentData(fullyUpdatedEntity);

        Assertions.assertEquals(fullyUpdatedEntity.getId(), result.getId());
        Assertions.assertEquals(fullyUpdatedEntity.getUploadedAt(), result.getUploadedAt());
        Assertions.assertNotEquals(documentEntity.getTextRecognitionResult(), result.getTextRecognitionResult());
    }


    @Test
    public void testUpdateTranslation() {
        DocumentsRepository documentsRepository = new TestDocumentRepository();
        DocumentEntity documentEntity = documentsRepository.getDocumentById("1");
        LocalDateTime currentTime = LocalDateTime.now();

        DocumentEntity updatedEntity = DocumentEntity.builder(documentEntity)
                .withTranslationResult(TranslationResult.builder(TranslationResult.ResultType.MANUAL)
                        .withTranslatedAt(currentTime)
                        .withTranslatedText("newTranslation")
                        .withSourceLanguage("sourceLanguage")
                        .withTargetLanguage("targetLanguage")
                        .withConfidence(100.0)
                        .build())
                .withModifiedAt(currentTime)
                .withModifiedBy("userName")
                .build();

        DocumentEntity result = documentsRepository.saveDocumentData(updatedEntity);
        Assertions.assertEquals(updatedEntity.getTranslationResult(), result.getTranslationResult());
        Assertions.assertEquals(updatedEntity.getModifiedAt(), result.getModifiedAt());
        Assertions.assertEquals(updatedEntity.getModifiedBy(), result.getModifiedBy());
    }
}
