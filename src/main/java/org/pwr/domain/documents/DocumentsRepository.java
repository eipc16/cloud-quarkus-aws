package org.pwr.domain.documents;

import org.pwr.infrastructure.dynamodb.DynamoDBService;
import org.pwr.infrastructure.dynamodb.DynamoDBTable;
import org.pwr.infrastructure.dynamodb.DynamoPage;
import org.pwr.infrastructure.dynamodb.DynamoPaginable;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Dependent
public class DocumentsRepository {

    private DocumentMapper documentMapper;
    private DynamoDBService dynamoDBService;

    @Inject
    public DocumentsRepository(DocumentMapper documentMapper, DynamoDBService dynamoDBService) {
        this.documentMapper = documentMapper;
        this.dynamoDBService = dynamoDBService;
    }

    public DocumentEntity saveDocumentData(DocumentEntity documentEntity) {
        DocumentDynamoEntity dynamoEntity = documentMapper.toDynamoEntity(documentEntity);
        DocumentDynamoEntity savedEntity = dynamoDBService.saveEntity(DocumentDynamoEntity.class, dynamoEntity);
        return documentMapper.toEntity(savedEntity);
    }

    public DynamoPage<DocumentEntity> getDocuments(DynamoPaginable dynamoPaginable, DocumentSearchFilter documentSearchFilter) {
        return dynamoDBService.getPage(DocumentDynamoEntity.class, dynamoPaginable, documentSearchFilter).mapTo(documentMapper::toEntity);
    }

    public DocumentEntity getDocumentById(String documentId) {
        return findDocumentById(documentId)
                .orElseThrow(() -> new NoSuchElementException("Cannot find document with id: " + documentId));
    }

    public Optional<DocumentEntity> findDocumentById(String documentId) {
        return dynamoDBService.getEntity(DocumentDynamoEntity.class, Map.of("id", AttributeValue.builder().s(documentId).build()))
                .map(documentMapper::toEntity);
    }
}
