package org.pwr.domain.documents;

import org.pwr.infrastructure.dynamodb.DynamoPage;
import org.pwr.infrastructure.dynamodb.DynamoPaginable;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface DocumentsRepository {
    DocumentEntity saveDocumentData(DocumentEntity documentEntity);

    DynamoPage<DocumentEntity> getDocuments(DynamoPaginable dynamoPaginable, DocumentSearchFilter documentSearchFilter);

    default DocumentEntity getDocumentById(String documentId) {
        return findDocumentById(documentId)
                .orElseThrow(() -> new NoSuchElementException("Cannot find document with id: " + documentId));
    }

    Optional<DocumentEntity> findDocumentById(String documentId);
}
