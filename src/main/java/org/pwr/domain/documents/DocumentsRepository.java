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

    DocumentEntity getDocumentById(String documentId);

    Optional<DocumentEntity> findDocumentById(String documentId);
}
