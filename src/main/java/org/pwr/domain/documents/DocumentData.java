package org.pwr.domain.documents;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.pwr.domain.buckets.MultipartBody;
import org.pwr.infrastructure.config.TranslateConfiguration;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

public class DocumentData extends MultipartBody {

    @Inject
    TranslateConfiguration translateConfiguration;

    @JsonProperty("sourceLang")
    @FormParam("sourceLang")
    @PartType(MediaType.TEXT_PLAIN)
    private String sourceLanguage;

    @JsonProperty("targetLang")
    @FormParam("targetLang")
    @PartType(MediaType.TEXT_PLAIN)
    private String targetLanguage;

    public String getSourceLanguage() {
        return Optional.ofNullable(sourceLanguage).orElse(translateConfiguration.getDefaultSourceLanguage());
    }

    public String getTargetLanguage() {
        return Optional.ofNullable(targetLanguage).orElse(translateConfiguration.getDefaultTargetLanguage());
    }
}
