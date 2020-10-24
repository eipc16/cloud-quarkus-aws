package org.pwr.domain.images;

import io.quarkus.runtime.StartupEvent;
import org.pwr.infrastructure.dynamodb.DynamoDBService;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
@Startup
public class ImagesRepository {

    private final DynamoDBService dynamoDBService;

    @Inject
    ImagesRepository(DynamoDBService dynamoDBService) {
        this.dynamoDBService = dynamoDBService;
    }

    public void onStart(@Observes StartupEvent startupEvent) {
        ImageDynamoEntity entityToCreate = ImageDynamoEntity.builder()
                .withId(7L)
                .withName("Test name")
                .withShortDescription("Description_6L")
                .withTime(LocalDateTime.now())
                .withListString(List.of(1L, 3L, 4L))
                .build();
        saveImage(entityToCreate);
        ImageDynamoEntity fetchedEntity = getById(7L);
        System.out.println(fetchedEntity.toString());
    }

    public boolean saveImage(ImageDynamoEntity imageDynamoEntity) {
        return dynamoDBService.saveEntity(imageDynamoEntity);
    }

    public ImageDynamoEntity getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new IllegalStateException(MessageFormat.format("Could not find entity with id: {0}", id)));
    }

    public Optional<ImageDynamoEntity> findById(Long id) {
        return dynamoDBService.getEntity(ImageDynamoEntity.class, Map.of("id", AttributeValue.builder().n(id.toString()).build()));
    }
}
