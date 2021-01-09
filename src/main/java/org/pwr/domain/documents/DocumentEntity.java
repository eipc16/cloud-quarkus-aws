package org.pwr.domain.documents;

import org.pwr.domain.buckets.FileDetails;
import org.pwr.domain.ocr.TextRecognitionResult;
import org.pwr.domain.translation.TranslationResult;

import java.time.LocalDateTime;
import java.util.Optional;

public class DocumentEntity {

    private String id;
    private String uploadedBy;
    private LocalDateTime uploadedAt;
    private String modifiedBy;
    private LocalDateTime modifiedAt;
    private FileDetails fileDetails;
    private TextRecognitionResult textRecognitionResult;
    private TranslationResult translationResult;

    private DocumentEntity() {
        //
    }

    private DocumentEntity(Builder builder) {
        id = builder.id;
        uploadedBy = builder.uploadedBy;
        uploadedAt = builder.uploadedAt;
        modifiedBy = builder.modifiedBy;
        modifiedAt = builder.modifiedAt;
        fileDetails = builder.fileDetails;
        textRecognitionResult = builder.textRecognitionResult;
        translationResult = builder.translationResult;
    }

    public Optional<String> getId() {
        return Optional.ofNullable(id);
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

    public FileDetails getFileDetails() {
        return fileDetails;
    }

    public Optional<TextRecognitionResult> getTextRecognitionResult() {
        return Optional.ofNullable(textRecognitionResult);
    }

    public Optional<TranslationResult> getTranslationResult() {
        return Optional.ofNullable(translationResult);
    }

    public static Builder builder(FileDetails fileDetails, String uploadedBy, LocalDateTime uploadedAt) {
        return new Builder(fileDetails, uploadedBy, uploadedAt);
    }

    public static Builder builder(DocumentEntity documentEntity) {
        return new Builder(documentEntity);
    }

    public static class Builder {
        private String id;
        private String uploadedBy;
        private LocalDateTime uploadedAt;
        private String modifiedBy;
        private LocalDateTime modifiedAt;
        private FileDetails fileDetails;
        private TextRecognitionResult textRecognitionResult;
        private TranslationResult translationResult;

        private Builder(FileDetails fileDetails, String uploadedBy, LocalDateTime uploadedAt) {
            this.fileDetails = fileDetails;
            this.uploadedBy = uploadedBy;
            this.uploadedAt = uploadedAt;
        }

        private Builder(DocumentEntity documentEntity) {
            id = documentEntity.id;
            uploadedBy = documentEntity.uploadedBy;
            uploadedAt = documentEntity.uploadedAt;
            modifiedBy = documentEntity.modifiedBy;
            modifiedAt = documentEntity.modifiedAt;
            fileDetails = documentEntity.fileDetails;
            textRecognitionResult = documentEntity.textRecognitionResult;
            translationResult = documentEntity.translationResult;
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withTextRecognitionResult(TextRecognitionResult textRecognitionResult) {
            this.textRecognitionResult = textRecognitionResult;
            return this;
        }

        public Builder withTranslationResult(TranslationResult translationResult) {
            this.translationResult = translationResult;
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

        public DocumentEntity build() {
            return new DocumentEntity(this);
        }
    }
}