package org.pwr.domain.documents;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.pwr.domain.buckets.MultipartBody;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

public class DocumentData extends MultipartBody {

    @JsonProperty("name")
    @FormParam("name")
    @PartType(MediaType.TEXT_PLAIN)
    private String name;

    @JsonProperty("sourceLang")
    @FormParam("sourceLang")
    @PartType(MediaType.TEXT_PLAIN)
    private String sourceLanguage;

    @JsonProperty("targetLang")
    @FormParam("targetLang")
    @PartType(MediaType.TEXT_PLAIN)
    private String targetLanguage;

    public String getName() {
        return name;
    }

    public String getSourceLanguage() {
        return Optional.ofNullable(sourceLanguage).orElse("en");
    }

    public String getTargetLanguage() {
        return Optional.ofNullable(targetLanguage).orElse("pl");
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }
}
