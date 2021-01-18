package org.pwr.documents;

import org.pwr.domain.buckets.FileDetails;
import org.pwr.domain.documents.DocumentDynamoEntity;
import org.pwr.domain.documents.DocumentEntity;
import org.pwr.domain.documents.DocumentSearchFilter;
import org.pwr.domain.documents.DocumentsRepository;
import org.pwr.infrastructure.dynamodb.DynamoPage;
import org.pwr.infrastructure.dynamodb.DynamoPaginable;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.LocalDateTime;
import java.util.*;

public class TestDocumentRepository implements DocumentsRepository {
    @Override
    public DocumentEntity saveDocumentData(DocumentEntity documentEntity) {
        return documentEntity;
    }

    @Override
    public DynamoPage<DocumentEntity> getDocuments(DynamoPaginable dynamoPaginable, DocumentSearchFilter documentSearchFilter) {
        DocumentEntity documentEntity = DocumentEntity.builder(new FileDetails(), "test", LocalDateTime.now()).build();
        List<DocumentEntity> list = new ArrayList<>();
        list.add(documentEntity);
        Map<String, AttributeValue> map = new HashMap<>();
        return new DynamoPage<>(list, 15, map, map);
    }

    @Override
    public DocumentEntity getDocumentById(String documentId) {
        return DocumentEntity.builder(new FileDetails(), "test", LocalDateTime.now()).build();
    }

    @Override
    public Optional<DocumentEntity> findDocumentById(String documentId) {
        return Optional.empty();
    }
}
