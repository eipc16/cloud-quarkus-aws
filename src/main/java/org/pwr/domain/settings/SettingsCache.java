package org.pwr.domain.settings;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@ApplicationScoped
public class SettingsCache {

    private SettingsEntity settings;
    private SettingsRepository settingsRepository;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private SettingsCache() {
        // empty for ApplicationScoped
    }

    @Inject
    public SettingsCache(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public SettingsEntity getSettings() {
//        readWriteLock.readLock().lock();
//        readWriteLock.writeLock().lock();
        if(settings == null) {
            settings = settingsRepository.getSettings();
        }
        SettingsEntity result = SettingsEntity.clone(settings);
//        readWriteLock.readLock().unlock();
//        readWriteLock.writeLock().unlock();
        return result;
    }

    public void refreshCache() {
        readWriteLock.writeLock().lock();
        settings = settingsRepository.getSettings();
        readWriteLock.writeLock().unlock();
    }
}
