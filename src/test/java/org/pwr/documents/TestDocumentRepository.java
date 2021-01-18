package org.pwr.documents;

import org.pwr.domain.buckets.FileDetails;
import org.pwr.domain.documents.DocumentEntity;
import org.pwr.domain.documents.DocumentSearchFilter;
import org.pwr.domain.documents.DocumentsRepository;
import org.pwr.domain.ocr.TextRecognitionResult;
import org.pwr.domain.translation.TranslationResult;
import org.pwr.infrastructure.dynamodb.DynamoPage;
import org.pwr.infrastructure.dynamodb.DynamoPaginable;
import org.pwr.infrastructure.qualifiers.TestBean;

import java.time.LocalDateTime;
import java.util.*;

@TestBean
public class TestDocumentRepository implements DocumentsRepository {

    private Map<String, DocumentEntity> dataSource = getData();

    static Map<String, DocumentEntity> getData() {
        Map<String, DocumentEntity> dataSource = new HashMap<>();
        dataSource.put("1", DocumentEntity.builder(new FileDetails("testBucket", "objectKey"), "userName", LocalDateTime.now())
                .withTextRecognitionResult(TextRecognitionResult.builder(TextRecognitionResult.ResultType.SUCCESS)
                        .withResult("success")
                        .withConfidence((double) 100)
                        .build())
                .withTranslationResult(TranslationResult.builder(TranslationResult.ResultType.SUCCESS)
                        .withTranslatedText("Translated text")
                        .build())
                .build());

        dataSource.put("2", DocumentEntity.builder(new FileDetails("testBucket", "objectKey"), "userName", LocalDateTime.now())
                .withTextRecognitionResult(TextRecognitionResult.builder(TextRecognitionResult.ResultType.SUCCESS)
                        .withResult("success")
                        .withConfidence((double) 100)
                        .build())
                .withTranslationResult(TranslationResult.builder(TranslationResult.ResultType.SUCCESS)
                        .withTranslatedText("Translated text")
                        .build())
                .build());

        return dataSource;
    }

    @Override
    public DocumentEntity saveDocumentData(DocumentEntity documentEntity) {
        String documentEntityId = documentEntity.getId()
                .orElseGet(() -> UUID.randomUUID().toString());
        DocumentEntity documentEntityToSave = DocumentEntity.builder(documentEntity)
                .withId(documentEntityId)
                .build();
        dataSource.put(documentEntityId, documentEntityToSave);
        return documentEntityToSave;
    }

    @Override
    public DynamoPage<DocumentEntity> getDocuments(DynamoPaginable dynamoPaginable, DocumentSearchFilter documentSearchFilter) {
        return new DynamoPage<>(dataSource.values(), dataSource.size(), Collections.emptyMap(), Collections.emptyMap());
    }

    @Override
    public Optional<DocumentEntity> findDocumentById(String documentId) {
        return Optional.ofNullable(dataSource.get(documentId));
    }
}
