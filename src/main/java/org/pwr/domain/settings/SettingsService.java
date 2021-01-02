package org.pwr.domain.settings;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class SettingsService {

    private SettingsCache settingsCache;
    private SettingsRepository settingsRepository;

    @Inject
    public SettingsService(SettingsCache settingsCache, SettingsRepository settingsRepository) {
        this.settingsCache = settingsCache;
        this.settingsRepository = settingsRepository;
    }

    public SettingsEntity getSettings() {
        return settingsCache.getSettings();
    }

    public SettingsEntity save(SettingsEntity settingsEntity) {
        SettingsEntity savedEntity = settingsRepository.save(settingsEntity);
        settingsCache.refreshCache();
        return savedEntity;
    }
}
