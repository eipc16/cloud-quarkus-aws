package org.pwr.documents;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.pwr.domain.buckets.FileDetails;
import org.pwr.domain.buckets.FileDownloadInformation;
import org.pwr.domain.buckets.StreamingResponse;
import org.pwr.domain.documents.*;
import org.pwr.domain.ocr.TextRecognitionResult;
import org.pwr.domain.translation.TranslationResult;
import org.pwr.infrastructure.dynamodb.DynamoPage;
import org.pwr.infrastructure.dynamodb.DynamoPaginable;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;


@QuarkusTest
public class DocumentsServiceTest {

    private DocumentsService documentsService = new DocumentsService(
            new TestDocumentsConfiguration(),
            new TestBucketServiceImpl(),
            new TestTextRecognitionService(),
            new TestTranslationService(),
            new TestDocumentRepository()
    );

    @Test
    public void testProcessDocument() throws FileNotFoundException {
        String userName = "user";
        DocumentData data = new DocumentData();
        data.setName("document");
        data.setSourceLanguage("pl");
        data.setTargetLanguage("en");
        data.fileName = "fileName";
        data.file = new FileInputStream("src/test/java/org/pwr/documents/test.jpg");
        LocalDateTime now = LocalDateTime.now();

        DocumentEntity expected = DocumentEntity.builder(new FileDetails("testBucket", "objectKey"), userName, now)
                .withName(data.getName())
                .withTextRecognitionResult(TextRecognitionResult.builder(TextRecognitionResult.ResultType.SUCCESS)
                        .withResult("success")
                        .withConfidence((double) 100)
                        .build())
                .withTranslationResult(TranslationResult.builder(TranslationResult.ResultType.SUCCESS)
                        .withTranslatedText("Translated text")
                        .build())
                .build();

        DocumentEntity processedEntity = documentsService.processDocument(data, "user");

        Assertions.assertEquals(expected.getTextRecognitionResult().flatMap(TextRecognitionResult::getResult), processedEntity.getTextRecognitionResult().flatMap(TextRecognitionResult::getResult));
        Assertions.assertEquals(expected.getTranslationResult().flatMap(TranslationResult::getTranslatedText), processedEntity.getTranslationResult().flatMap(TranslationResult::getTranslatedText));
    }

    @Test
    public void testGetDocuments() {
        DynamoPaginable dynamoPaginable = new DynamoPaginable();
        DocumentSearchFilter documentSearchFilter = new DocumentSearchFilter();

        DynamoPage<DocumentEntity> dynamoPage = documentsService.getDocuments(dynamoPaginable, documentSearchFilter);

        Assertions.assertEquals(2, dynamoPage.getContent().size());
    }

    @Test
    public void testGetDocumentById() {
        DocumentEntity expected = DocumentEntity.builder(new FileDetails("testBucket", "objectKey"), "userName", LocalDateTime.now())
                .withTextRecognitionResult(TextRecognitionResult.builder(TextRecognitionResult.ResultType.SUCCESS)
                        .withResult("success")
                        .withConfidence((double) 100)
                        .build())
                .withTranslationResult(TranslationResult.builder(TranslationResult.ResultType.SUCCESS)
                        .withTranslatedText("Translated text")
                        .build())
                .build();

        DocumentEntity documentEntity = documentsService.getDocumentById("1");

        Assertions.assertEquals(expected.getTextRecognitionResult().flatMap(TextRecognitionResult::getResult), documentEntity.getTextRecognitionResult().flatMap(TextRecognitionResult::getResult));
        Assertions.assertEquals(expected.getTranslationResult().flatMap(TranslationResult::getTranslatedText), documentEntity.getTranslationResult().flatMap(TranslationResult::getTranslatedText));
    }

    @Test
    public void testDownloadDocument() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamingResponse expectedOutput = new StreamingResponse("image/png", baos::writeTo);

        FileDownloadInformation downloadInformation = documentsService.downloadDocument("1");

        Assertions.assertEquals(expectedOutput.getContentType(), downloadInformation.getStreamingResponse().getContentType());
    }

    @Test
    public void testUpdateTextRecognition() {
        String newTextRecognition = "New text recognition";
        DocumentEntity expected = DocumentEntity.builder(new FileDetails("testBucket", "objectKey"), "userName", LocalDateTime.now())
                .withTextRecognitionResult(TextRecognitionResult.builder(TextRecognitionResult.ResultType.SUCCESS)
                        .withResult(newTextRecognition)
                        .withConfidence((double) 100)
                        .build())
                .withTranslationResult(TranslationResult.builder(TranslationResult.ResultType.SUCCESS)
                        .withTranslatedText("Translated text")
                        .build())
                .build();

        DocumentEntity documentEntity = documentsService.updateTextRecognition("1", newTextRecognition, "userName");

        Assertions.assertEquals(expected.getTextRecognitionResult().flatMap(TextRecognitionResult::getResult), documentEntity.getTextRecognitionResult().flatMap(TextRecognitionResult::getResult));
    }


    @Test
    public void testUpdateTranslation() {
        String newTranslation = "New translated text";
        DocumentEntity expected = DocumentEntity.builder(new FileDetails("testBucket", "objectKey"), "userName", LocalDateTime.now())
                .withTextRecognitionResult(TextRecognitionResult.builder(TextRecognitionResult.ResultType.SUCCESS)
                        .withResult("success")
                        .withConfidence((double) 100)
                        .build())
                .withTranslationResult(TranslationResult.builder(TranslationResult.ResultType.SUCCESS)
                        .withTranslatedText(newTranslation)
                        .build())
                .build();

        DocumentEntity result = documentsService.updateTranslation("1", newTranslation, "pl", "en", "userName");
        Assertions.assertEquals(expected.getTranslationResult().flatMap(TranslationResult::getTranslatedText), result.getTranslationResult().flatMap(TranslationResult::getTranslatedText));
    }
}
