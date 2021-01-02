package org.pwr.infrastructure.config;

import io.quarkus.arc.config.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ConfigProperties(prefix = "documents")
public class DocumentsConfiguration {

    private static final String DEFAULT_DOCUMENTS_BUCKET = "bitbeat-bucket";

    @ConfigProperty(name = "bucket.name", defaultValue = DEFAULT_DOCUMENTS_BUCKET)
    public String bucketName;

    public String getBucket() {
        return bucketName;
    }
}
