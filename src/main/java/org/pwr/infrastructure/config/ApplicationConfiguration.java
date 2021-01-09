package org.pwr.infrastructure.config;

import io.quarkus.arc.config.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ConfigProperties(prefix = "application")
public class ApplicationConfiguration {

    private static final String DEFAULT_DEBUG_VALUE = "false";

    @ConfigProperty(name = "debug.enabled", defaultValue = DEFAULT_DEBUG_VALUE)
    public boolean isDebugEnabled;

    public boolean isDebugEnabled() {
        return isDebugEnabled;
    }
}
