package org.pwr.documents;

import org.pwr.domain.buckets.FileDetails;
import org.pwr.domain.documents.DocumentDynamoEntity;
import org.pwr.domain.documents.DocumentEntity;
import org.pwr.domain.documents.DocumentSearchFilter;
import org.pwr.domain.documents.DocumentsRepository;
import org.pwr.infrastructure.dynamodb.DynamoPage;
import org.pwr.infrastructure.dynamodb.DynamoPaginable;
import org.pwr.infrastructure.qualifiers.TestBean;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.LocalDateTime;
import java.util.*;

@TestBean
public class TestDocumentRepository implements DocumentsRepository {

    private Map<String, DocumentEntity> dataSource = new HashMap<>();

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
        return new DynamoPage<DocumentEntity>(dataSource.values(), dataSource.size(), Collections.emptyMap(), Collections.emptyMap()); //upraszczamy sobie trochę sprawę bo trudno to zamockować
    }

    @Override
    public Optional<DocumentEntity> findDocumentById(String documentId) {
        return Optional.ofNullable(dataSource.get(documentId));
    }
}
