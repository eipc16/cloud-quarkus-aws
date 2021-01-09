package org.pwr.domain.settings;

import org.pwr.infrastructure.config.TesseractConfiguration;
import org.pwr.infrastructure.config.TranslateConfiguration;
import org.pwr.infrastructure.dynamodb.DynamoDBService;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Map;

@Dependent
class SettingsRepository {

    private final DynamoDBService dynamoDBService;
    private final TesseractConfiguration tesseractConfiguration;
    private final TranslateConfiguration translateConfiguration;

    @Inject
    SettingsRepository(DynamoDBService dynamoDBService,
                       TesseractConfiguration tesseractConfiguration,
                       TranslateConfiguration translateConfiguration) {
        this.dynamoDBService = dynamoDBService;
        this.tesseractConfiguration = tesseractConfiguration;
        this.translateConfiguration = translateConfiguration;
    }

    public SettingsEntity getSettings() {
        return dynamoDBService.getEntity(SettingsDynamoEntity.class, Map.of("id", AttributeValue.builder().n("0").build()))
                .map(this::mapToEntity)
                .orElseGet(this::createDefaultSettings);
    }

    private SettingsEntity createDefaultSettings() {
        SettingsEntity defaultSettings = SettingsEntity.builder()
                .withOCRInsufficientConfidenceThreshold(tesseractConfiguration.getDefaultThreshold())
                .withTranslateInsufficientConfidenceThreshold(translateConfiguration.getDefaultThreshold())
                .build();
        return save(defaultSettings);
    }

    private SettingsEntity mapToEntity(SettingsDynamoEntity settingsDynamoEntity) {
        return SettingsEntity.builder()
                .withOCRInsufficientConfidenceThreshold(settingsDynamoEntity.getOcrInsufficientConfidenceThreshold())
                .withTranslateInsufficientConfidenceThreshold(settingsDynamoEntity.getTranslateInsufficientConfidenceThreshold())
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
                .withTranslateInsufficientConfidenceThreshold(settingsEntity.getTranslateInsufficientConfidenceThreshold())
                .build();
    }
}
