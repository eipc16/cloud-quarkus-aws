package org.pwr.domain.settings;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SettingsMapper {

    SettingsEntity toEntity(SettingsDTO settingsDTO) {
        return SettingsEntity.builder()
                .withOCRInsufficientConfidenceThreshold(settingsDTO.getOCRTreshold())
                .withTranslateInsufficientConfidenceThreshold(settingsDTO.getTranslationThreshold())
                .build();
    }

    SettingsDTO toDTO(SettingsEntity settingsEntity) {
        return SettingsDTO.builder()
                .withOCRThreshold(settingsEntity.getOcrInsufficientConfidenceThreshold())
                .withTranslationThreshold(settingsEntity.getTranslateInsufficientConfidenceThreshold())
                .build();
    }
}
