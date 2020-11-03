package org.pwr.infrastructure.dynamodb;

import org.jboss.logging.Logger;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;

@Dependent
public class DynamoDBService {

    private static final Logger LOGGER = Logger.getLogger(DynamoDBService.class);

    private final DynamoDbClient dynamoDbClient;
    private final DynamoDBObjectMapper objectMapper;
    private static DynamoDbWaiter dynamoDbWaiter;

    @Inject
    public DynamoDBService(DynamoDBObjectMapper objectMapper) {
        this.dynamoDbClient = DynamoDbClient.builder().region(Region.US_EAST_1).build();
        dynamoDbWaiter = dynamoDbClient.waiter();
        this.objectMapper = objectMapper;
    }

    void createTable(TableDefinition tableDefinition) {
        LOGGER.infof("Requested to create table %s...", tableDefinition.getTableName());
        LOGGER.infof("Table %s with key %s, with attributes %s", tableDefinition.getTableName(), tableDefinition.getKeys(), tableDefinition.getAttributeDefinitions());
        if (tableDefinition.isForceRebuild()) {
            deleteTable(tableDefinition.getTableName());
        }
        if (findTable(tableDefinition.getTableName()).isEmpty()) {
            createTable(tableDefinition.asCreateTableRequest());
        }
    }

    void deleteTable(String tableName) {
        deleteTable(DeleteTableRequest.builder()
                .tableName(tableName)
                .build());
    }

    private boolean deleteTable(DeleteTableRequest deleteTableRequest) {
        try {
            DeleteTableResponse createTableResponse = dynamoDbClient.deleteTable(deleteTableRequest);
            WaiterResponse<DescribeTableResponse> waiterResponse = dynamoDbWaiter.waitUntilTableNotExists(r -> r.tableName(deleteTableRequest.tableName()));
            waiterResponse.matched().response().ifPresent(System.out::println);
            LOGGER.infof("Removed table %s.", createTableResponse.tableDescription().tableName());
            return true;
        } catch (DynamoDbException ex) {
            LOGGER.errorf("Could not remove table: %s. Exception: %s", deleteTableRequest.tableName(), ex.getMessage());
        }
        return false;
    }

    Optional<TableDescription> findTable(String tableName) {
        try {
            DescribeTableRequest request = DescribeTableRequest.builder().tableName(tableName).build();
            DescribeTableResponse createTableResponse = dynamoDbClient.describeTable(request);
            return Optional.of(createTableResponse.table());
        } catch (DynamoDbException ex) {
            LOGGER.errorf("Table not found: %s.", tableName, ex.getMessage());
        }
        return Optional.empty();
    }

    private Optional<String> createTable(CreateTableRequest createTableRequest) {
        try {
            CreateTableResponse createTableResponse = dynamoDbClient.createTable(createTableRequest);
            WaiterResponse<DescribeTableResponse> waiterResponse = dynamoDbWaiter.waitUntilTableExists(r -> r.tableName(createTableRequest.tableName()));
            waiterResponse.matched().response().ifPresent(System.out::println);
            LOGGER.infof("Created table %s.", createTableResponse.tableDescription().tableName());
            return Optional.of(createTableResponse.tableDescription().tableName());
        } catch (DynamoDbException ex) {
            LOGGER.errorf("Could not create table: %s. Exception: %s", createTableRequest.tableName(), ex.getMessage());
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public <T> T saveEntity(Class<T> clazz, Object object) {
        if (!isDynamoEntity(object)) {
            throw new IllegalStateException(MessageFormat.format(
                    "Cannot save {0} entity because it does not belong to any DynamoDBTable", object.getClass().getSimpleName()));
        }
        String tableName = object.getClass().getAnnotation(DynamoDBTable.class).name();
        Map<String, AttributeValue> values = objectMapper.mapEntityToValuesByNames(object);
        PutItemRequest request = createPutItemRequest(tableName, values);
        saveEntity(request);
        return objectMapper.mapToEntity(clazz, values);
    }

    private PutItemRequest createPutItemRequest(String tableName, Map<String, AttributeValue> values) {
        return PutItemRequest.builder()
                .tableName(tableName)
                .item(values)
                .build();
    }

    private <T extends Object> boolean isDynamoEntity(T object) {
        return isDynamoEntity(object.getClass());
    }

    private boolean isDynamoEntity(Class<?> clazz) {
        return clazz.isAnnotationPresent(DynamoDBTable.class);
    }

    private <T> boolean saveEntity(PutItemRequest putItemRequest) {
        try {
            dynamoDbClient.putItem(putItemRequest);
            return true;
        } catch (DynamoDbException ex) {
            LOGGER.errorf("Could not save entity to table: %s. Exception: %s", putItemRequest.tableName(), ex.getMessage());
        }
        return false;
    }

    public <T> Optional<T> getEntity(Class<T> targetClass, Map<String, AttributeValue> keys) {
        if (!isDynamoEntity(targetClass)) {
            throw new IllegalStateException(MessageFormat.format(
                    "Cannot fetch {0} entity because it does not belong to any DynamoDBTable", targetClass.getSimpleName()));
        }
        String tableName = targetClass.getAnnotation(DynamoDBTable.class).name();
        return getEntity(targetClass, createGetItemRequest(tableName, keys));
    }

    private GetItemRequest createGetItemRequest(String tableName, Map<String, AttributeValue> keys) {
        return GetItemRequest.builder()
                .tableName(tableName)
                .key(keys)
                .build();
    }

    private <T> Optional<T> getEntity(Class<T> targetClass, GetItemRequest request) {
        try {
            GetItemResponse response = dynamoDbClient.getItem(request);
            return Optional.ofNullable(objectMapper.mapToEntity(targetClass, response.item()));
        } catch (ResourceNotFoundException ex) {
            return Optional.empty();
        }
    }

    public <T> boolean deleteEntity(Class<T> targetClass, Map<String, AttributeValue> keys) {
        if (!isDynamoEntity(targetClass)) {
            throw new IllegalStateException(MessageFormat.format(
                    "Cannot delete {0} entity because it does not belong to any DynamoDBTable", targetClass.getSimpleName()));
        }
        String tableName = targetClass.getAnnotation(DynamoDBTable.class).name();
        return deleteEntity(createDeleteItemRequest(tableName, keys));
    }

    private DeleteItemRequest createDeleteItemRequest(String tableName, Map<String, AttributeValue> keys) {
        return DeleteItemRequest.builder()
                .tableName(tableName)
                .key(keys)
                .build();
    }

    private boolean deleteEntity(DeleteItemRequest request) {
        try {
            dynamoDbClient.deleteItem(request);
            return true;
        } catch (ResourceNotFoundException ex) {
            LOGGER.warnf("Could not remove entity. Cause: {}", ex.getMessage());
        }
        return false;
    }
}
