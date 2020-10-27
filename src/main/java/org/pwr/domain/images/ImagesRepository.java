package org.pwr.domain.images;

import org.pwr.infrastructure.dynamodb.DynamoDBService;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;

@Dependent
public class ImagesRepository {

    private final DynamoDBService dynamoDBService;

    @Inject
    ImagesRepository(DynamoDBService dynamoDBService) {
        this.dynamoDBService = dynamoDBService;
    }

    public ImageDynamoEntity saveImage(ImageDynamoEntity imageDynamoEntity) {
        return dynamoDBService.saveEntity(ImageDynamoEntity.class, imageDynamoEntity);
    }

    public ImageDynamoEntity getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new IllegalStateException(MessageFormat.format("Could not find entity with id: {0}", id)));
    }

    public Optional<ImageDynamoEntity> findById(Long id) {
        return dynamoDBService.getEntity(ImageDynamoEntity.class, Map.of("id", AttributeValue.builder().n(id.toString()).build()));
    }

    public boolean deleteImage(Long id) {
        return dynamoDBService.deleteEntity(ImageDynamoEntity.class, Map.of("id", AttributeValue.builder().n(id.toString()).build()));
    }
}
