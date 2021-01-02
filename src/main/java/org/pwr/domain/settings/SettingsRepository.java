package org.pwr.domain.settings;

import org.pwr.infrastructure.dynamodb.DynamoDBService;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Map;

@Dependent
class SettingsRepository {

    private final DynamoDBService dynamoDBService;

    @Inject
    SettingsRepository(DynamoDBService dynamoDBService) {
        this.dynamoDBService = dynamoDBService;
    }

    public SettingsEntity getSettings() {
        return dynamoDBService.getEntity(SettingsDynamoEntity.class, Map.of("id", AttributeValue.builder().n("0").build()))
                .map(this::mapToEntity)
                .orElseThrow(() -> new RuntimeException("Settings not found"));
    }

    private SettingsEntity mapToEntity(SettingsDynamoEntity settingsDynamoEntity) {
        return SettingsEntity.builder()
                .withOCRInsufficientConfidenceThreshold(settingsDynamoEntity.getOcrInsufficientConfidenceThreshold())
                .build();
    }

    public SettingsEntity save(SettingsEntity settingsEntity) {
        SettingsDynamoEntity dynamoEntity = mapToDynamoEntity(settingsEntity);
        SettingsDynamoEntity savedEntity = dynamoDBService.saveEntity(SettingsDynamoEntity.class, dynamoEntity);
        return mapToEntity(savedEntity);
    }

    private SettingsDynamoEntity mapToDynamoEntity(SettingsEntity settingsEntity) {
        return SettingsDynamoEntity.builder()
                .withOCRInsufficientConfidenceThreshold(settingsEntity.getOcrInsufficientConfidenceThreshold())
                .build();
    }
}
