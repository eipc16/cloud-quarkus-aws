package org.pwr.infrastructure.dynamodb;

import org.jboss.logging.Logger;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;

@ApplicationScoped
class DynamoDBService {

    private static final Logger LOGGER = Logger.getLogger(DynamoDBService.class);

    private final DynamoDbClient dynamoDbClient;
    private static DynamoDbWaiter dynamoDbWaiter;

    @Inject
    public DynamoDBService(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
        dynamoDbWaiter = dynamoDbClient.waiter();
    }

    void createTable(TableDefinition tableDefinition) {
        LOGGER.infof("Requested to create table %s...", tableDefinition.getTableName());
        LOGGER.infof("Table %s with key %s, with attributes %s", tableDefinition.getTableName(), tableDefinition.getKeys(), tableDefinition.getAttributeDefinitions());
        if (tableDefinition.isForceRebuild()) {
            deleteTable(tableDefinition.getTableName());
        }
        createTable(tableDefinition.asCreateTableRequest());
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
}
