package org.pwr.domain.documents;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.pwr.domain.buckets.FileDetails;
import org.pwr.domain.ocr.TextRecognitionResult;
import org.pwr.domain.translation.TranslationResult;

import java.time.LocalDateTime;
import java.util.Optional;

public class DocumentDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("uploadedBy")
    private String uploadedBy;

    @JsonProperty("uploadedAt")
    private LocalDateTime uploadedAt;

    @JsonProperty("modifiedBy")
    private String modifiedBy;

    @JsonProperty("modifiedAt")
    private LocalDateTime modifiedAt;

    @JsonProperty("originalName")
    private String originalName;

    @JsonProperty("ocrResult")
    private ProcessingStatus ocrResult;

    @JsonProperty("ocrConfidence")
    private double ocrConfidence;

    @JsonProperty("translationResult")
    private ProcessingStatus translationResult;

    @JsonProperty("translationConfidence")
    private double translationConfidence;

    @JsonGetter("uploadedAt")
    public String getUploadedAtAsString() {
        return Optional.ofNullable(uploadedAt)
                .map(LocalDateTime::toString)
                .orElse(null);
    }

    @JsonGetter("modifiedAt")
    public String getModifiedAtAsString() {
        return Optional.ofNullable(modifiedAt)
                .map(LocalDateTime::toString)
                .orElse(null);
    }

    private DocumentDTO() {
        //
    }

    private DocumentDTO(Builder builder) {
        id = builder.id;
        name = builder.name;
        uploadedBy = builder.uploadedBy;
        uploadedAt = builder.uploadedAt;
        modifiedBy = builder.modifiedBy;
        modifiedAt = builder.modifiedAt;
        originalName = builder.originalName;
        ocrResult = builder.ocrResult;
        ocrConfidence = builder.ocrConfidence;
        translationResult = builder.translationResult;
        translationConfidence = builder.translationConfidence;
    }

    public static Builder builder(FileDetails fileDetails) {
        return new Builder(fileDetails);
    }

    public static Builder builder(DocumentDTO documentEntity) {
        return new Builder(documentEntity);
    }

    public static class Builder {
        private String id;
        private String name;
        private String uploadedBy;
        private LocalDateTime uploadedAt;
        private String modifiedBy;
        private LocalDateTime modifiedAt;
        private String originalName;
        private ProcessingStatus ocrResult;
        private double ocrConfidence;
        private ProcessingStatus translationResult;
        private double translationConfidence;

        private Builder(FileDetails fileDetails) {
            this.originalName = fileDetails.getOriginalName().orElse(fileDetails.getObjectKey());
        }

        private Builder(DocumentDTO documentEntity) {
            id = documentEntity.id;
            name = documentEntity.name;
            uploadedBy = documentEntity.uploadedBy;
            uploadedAt = documentEntity.uploadedAt;
            modifiedBy = documentEntity.modifiedBy;
            modifiedAt = documentEntity.modifiedAt;
            originalName = documentEntity.originalName;
            ocrResult = documentEntity.ocrResult;
            ocrConfidence = documentEntity.ocrConfidence;
            translationResult = documentEntity.translationResult;
            translationConfidence = documentEntity.translationConfidence;
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withTextRecognitionResult(ProcessingStatus processingStatus) {
            this.ocrResult = processingStatus;
            return this;
        }

        public Builder withTextRecognitionConfidence(double confidence) {
            this.ocrConfidence = confidence;
            return this;
        }

        public Builder withTranslationResult(ProcessingStatus processingStatus) {
            this.translationResult = processingStatus;
            return this;
        }

        public Builder withTranslationConfidence(double confidence) {
            this.translationConfidence = confidence;
            return this;
        }

        public Builder withUploadedBy(String uploadedBy) {
            this.uploadedBy = uploadedBy;
            return this;
        }

        public Builder withUploadedAt(LocalDateTime uploadedAt) {
            this.uploadedAt = uploadedAt;
            return this;
        }

        public Builder withModifiedBy(String modifiedBy) {
            this.modifiedBy = modifiedBy;
            return this;
        }

        public Builder withModifiedAt(LocalDateTime modifiedAt) {
            this.modifiedAt = modifiedAt;
            return this;
        }

        public DocumentDTO build() {
            return new DocumentDTO(this);
        }
    }
}