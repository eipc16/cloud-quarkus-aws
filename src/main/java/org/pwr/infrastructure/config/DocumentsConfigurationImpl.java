package org.pwr.infrastructure.config;

import io.quarkus.arc.config.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.inject.Default;

@ConfigProperties(prefix = "documents")
@Default
public class DocumentsConfigurationImpl implements DocumentsConfiguration {

    private static final String DEFAULT_DOCUMENTS_BUCKET = "bitbeat-bucket";

    @ConfigProperty(name = "bucket.name", defaultValue = DEFAULT_DOCUMENTS_BUCKET)
    public String bucketName;

    public String getBucket() {
        return bucketName;
    }
}
