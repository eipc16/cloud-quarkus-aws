package org.pwr.domain.documents;

import org.pwr.domain.buckets.FileDetails;
import org.pwr.infrastructure.dynamodb.AttributeConverter;
import org.pwr.infrastructure.dynamodb.DynamoDBTable;
import org.pwr.infrastructure.dynamodb.convertes.FileDetailsConverter;
import org.pwr.infrastructure.dynamodb.convertes.LocalDateTimeConverter;

import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@DynamoDBTable("documents")
public class DocumentDynamoEntity {

    @Id
    private String id;

    @AttributeConverter(FileDetailsConverter.class)
    private FileDetails fileDetails;

    private String name;

    private String uploadedBy;

    @AttributeConverter(LocalDateTimeConverter.class)
    private LocalDateTime uploadedAt;

    private String modifiedBy;

    @AttributeConverter(LocalDateTimeConverter.class)
    private LocalDateTime modifiedAt;

    private Double ocrConfidence;

    @AttributeConverter(ProcessingStatusConverter.class)
    private ProcessingStatus ocrStatus;

    private String ocrResult;

    @AttributeConverter(LocalDateTimeConverter.class)
    private LocalDateTime ocrProcessedAt;

    private Double translationConfidence;

    @AttributeConverter(ProcessingStatusConverter.class)
    private ProcessingStatus translationStatus;

    @AttributeConverter(LocalDateTimeConverter.class)
    private LocalDateTime translatedAt;

    private String translationResult;

    private String sourceLanguage;

    private String targetLanguage;

    public DocumentDynamoEntity() {
        // empty for deserialization
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public FileDetails getFileDetails() {
        return fileDetails;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public Optional<String> getModifiedBy() {
        return Optional.ofNullable(modifiedBy);
    }

    public Optional<LocalDateTime> getModifiedAt() {
        return Optional.ofNullable(modifiedAt);
    }

    public Optional<Double> getOCRConfidence() {
        return Optional.ofNullable(ocrConfidence);
    }

    public ProcessingStatus getOcrStatus() {
        return ocrStatus;
    }

    public Optional<LocalDateTime> getOcrProcessedAt() {
        return Optional.ofNullable(ocrProcessedAt);
    }

    public Optional<String> getOCRResult() {
        return Optional.ofNullable(ocrResult);
    }

    public Optional<Double> getTranslationConfidence() {
        return Optional.ofNullable(translationConfidence);
    }

    public ProcessingStatus getTranslationStatus() {
        return translationStatus;
    }

    public Optional<LocalDateTime> getTranslationProcessedAt() {
        return Optional.ofNullable(translatedAt);
    }

    public Optional<String> getTranslationResult() {
        return Optional.ofNullable(translationResult);
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    private DocumentDynamoEntity(Builder builder) {
        id = Optional.ofNullable(builder.id)
                .orElseGet(() -> UUID.randomUUID().toString());
        name = Optional.ofNullable(builder.name)
                .orElseGet(() -> builder.fileDetails.getOriginalName().orElse("<unknown>"));
        fileDetails = builder.fileDetails;
        uploadedBy = builder.uploadedBy;
        uploadedAt = builder.uploadedAt;
        modifiedBy = builder.modifiedBy;
        modifiedAt = builder.modifiedAt;
        ocrConfidence = builder.ocrConfidence;
        ocrStatus = builder.ocrStatus;
        ocrResult = builder.ocrResult;
        ocrProcessedAt = builder.ocrProcessedAt;
        translationConfidence = builder.translationConfidence;
        translationStatus = builder.translationStatus;
        translatedAt = builder.translatedAt;
        translationResult = builder.translationResult;
        sourceLanguage = builder.sourceLanguage;
        targetLanguage = builder.targetLanguage;
    }

    public static Builder builder(FileDetails fileDetails) {
        return new Builder(fileDetails);
    }

    public static Builder builder(DocumentDynamoEntity documentDynamoEntity) {
        return new Builder(documentDynamoEntity);
    }

    static class Builder {
        private String id;
        private String name;
        private FileDetails fileDetails;
        private String uploadedBy;
        private LocalDateTime uploadedAt;
        private String modifiedBy;
        private LocalDateTime modifiedAt;
        private Double ocrConfidence;
        private ProcessingStatus ocrStatus;
        private String ocrResult;
        private LocalDateTime ocrProcessedAt;
        private Double translationConfidence;
        private ProcessingStatus translationStatus;
        private LocalDateTime translatedAt;
        private String translationResult;
        private String sourceLanguage;
        private String targetLanguage;

        private Builder(FileDetails fileDetails) {
            this.fileDetails = fileDetails;
        }

        private Builder(DocumentDynamoEntity documentDynamoEntity) {
            id = documentDynamoEntity.id;
            name = documentDynamoEntity.name;
            fileDetails = documentDynamoEntity.fileDetails;
            uploadedBy = documentDynamoEntity.uploadedBy;
            uploadedAt = documentDynamoEntity.uploadedAt;
            modifiedBy = documentDynamoEntity.modifiedBy;
            modifiedAt = documentDynamoEntity.modifiedAt;
            ocrConfidence = documentDynamoEntity.ocrConfidence;
            ocrStatus = documentDynamoEntity.ocrStatus;
            ocrResult = documentDynamoEntity.ocrResult;
            ocrProcessedAt = documentDynamoEntity.ocrProcessedAt;
            translationConfidence = documentDynamoEntity.translationConfidence;
            translationStatus = documentDynamoEntity.translationStatus;
            translatedAt = documentDynamoEntity.translatedAt;
            translationResult = documentDynamoEntity.translationResult;
            sourceLanguage = documentDynamoEntity.sourceLanguage;
            targetLanguage = documentDynamoEntity.targetLanguage;
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withUploadedBy(String userName) {
            this.uploadedBy = userName;
            return this;
        }

        public Builder withUploadedAt(LocalDateTime uploadedAt) {
            this.uploadedAt = uploadedAt;
            return this;
        }

        public Builder withModifiedBy(String userName) {
            this.modifiedBy = userName;
            return this;
        }

        public Builder withModifiedAt(LocalDateTime modifiedAt) {
            this.modifiedAt = modifiedAt;
            return this;
        }

        public Builder withOCRConfidence(double ocrConfidence) {
            this.ocrConfidence = ocrConfidence;
            return this;
        }

        public Builder wthOCRProcessingStatus(ProcessingStatus processingStatus) {
            this.ocrStatus = processingStatus;
            return this;
        }

        public Builder withOCRResult(String ocrResult) {
            this.ocrResult = ocrResult;
            return this;
        }

        public Builder withOCRProcessedAt(LocalDateTime ocrProcessedAt) {
            this.ocrProcessedAt = ocrProcessedAt;
            return this;
        }

        public Builder withTranslationConfidence(Double translationConfidence) {
            this.translationConfidence = translationConfidence;
            return this;
        }

        public Builder withTranslationProcessingStatus(ProcessingStatus translationProcessingStatus) {
            this.translationStatus = translationProcessingStatus;
            return this;
        }

        public Builder withTranslatedAt(LocalDateTime translatedAt) {
            this.translatedAt = translatedAt;
            return this;
        }

        public Builder withTranslationResult(String translationResult) {
            this.translationResult = translationResult;
            return this;
        }

        public Builder withSourceLanguage(String sourceLanguage) {
            this.sourceLanguage = sourceLanguage;
            return this;
        }

        public Builder withTargetLanguage(String targetLanguage) {
            this.targetLanguage = targetLanguage;
            return this;
        }

        public DocumentDynamoEntity build() {
            return new DocumentDynamoEntity(this);
        }
    }
}
