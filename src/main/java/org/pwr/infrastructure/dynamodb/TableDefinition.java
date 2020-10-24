package org.pwr.infrastructure.dynamodb;

import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class TableDefinition {

    private final String tableName;
    private final Set<KeySchemaElement> keys;
    private final Set<AttributeDefinition> attributeDefinitions;
    private final ProvisionedThroughput throughput;
    private final boolean forceRebuild;

    private TableDefinition(Builder builder) {
        tableName = builder.tableName;
        keys = builder.keys;
        attributeDefinitions = builder.attributeDefinitions;
        throughput = Optional.ofNullable(builder.throughput).orElseGet(this::getDefaultThroughput);
        forceRebuild = builder.forceRebuild;
    }

    public String getTableName() {
        return tableName;
    }

    public Set<KeySchemaElement> getKeys() {
        return keys;
    }

    public Set<AttributeDefinition> getAttributeDefinitions() {
        return attributeDefinitions;
    }

    public boolean isForceRebuild() {
        return forceRebuild;
    }

    private ProvisionedThroughput getDefaultThroughput() {
        return ProvisionedThroughput.builder()
                .readCapacityUnits(5L)
                .writeCapacityUnits(6L)
                .build();
    }

    public CreateTableRequest asCreateTableRequest() {
        return CreateTableRequest.builder()
                .tableName(tableName)
                .keySchema(keys)
                .attributeDefinitions(attributeDefinitions)
                .provisionedThroughput(throughput)
                .build();
    }

    public UpdateTableRequest asUpdateTableRequest() {
        return UpdateTableRequest.builder()
                .tableName(tableName)
                .attributeDefinitions(attributeDefinitions)
                .provisionedThroughput(throughput)
                .build();
    }

    public static Builder builder(String tableName) {
        return new Builder(tableName);
    }

    public static class Builder {
        private final String tableName;
        private final Set<KeySchemaElement> keys = new HashSet<>();
        private final Set<AttributeDefinition> attributeDefinitions = new HashSet<>();
        private ProvisionedThroughput throughput;
        private boolean forceRebuild = false;

        Builder(String tableName) {
            this.tableName = tableName;
        }

        public Builder addKey(String attributeName, KeyType type) {
            return addKey(KeySchemaElement.builder()
                    .attributeName(attributeName)
                    .keyType(type)
                    .build());
        }

        public Builder addKey(KeySchemaElement key) {
            this.keys.add(key);
            return this;
        }

        public Builder withKeys(Collection<KeySchemaElement> keys) {
            this.keys.clear();
            this.keys.addAll(keys);
            return this;
        }

        public Builder addAttribute(String attributeName, ScalarAttributeType type) {
            return addAttribute(AttributeDefinition.builder()
                    .attributeName(attributeName)
                    .attributeType(type)
                    .build());
        }

        public Builder addAttribute(AttributeDefinition attributeDefinition) {
            this.attributeDefinitions.add(attributeDefinition);
            return this;
        }

        public Builder withAttributes(Collection<AttributeDefinition> attributeDefinitions) {
            this.attributeDefinitions.clear();
            this.attributeDefinitions.addAll(attributeDefinitions);
            return this;
        }

        public Builder withTroughput(ProvisionedThroughput troughput) {
            this.throughput = troughput;
            return this;
        }

        public Builder withForceRebuild(boolean forceRebuild) {
            this.forceRebuild = forceRebuild;
            return this;
        }

        public TableDefinition build() {
            return new TableDefinition(this);
        }
    }
}
