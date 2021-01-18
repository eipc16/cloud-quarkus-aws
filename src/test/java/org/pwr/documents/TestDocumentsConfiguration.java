package org.pwr.documents;

import org.pwr.infrastructure.config.DocumentsConfiguration;
import org.pwr.infrastructure.qualifiers.TestBean;

@TestBean
public class TestDocumentsConfiguration implements DocumentsConfiguration {
    @Override
    public String getBucket() {
        return "testBucket";
    }
}
