package org.pwr.infrastructure.dynamodb.convertes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pwr.domain.buckets.FileDetails;
import org.pwr.infrastructure.dynamodb.DynamoDBTypeConverter;

public class FileDetailsConverter implements DynamoDBTypeConverter<FileDetails> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String convert(FileDetails target) {
        try {
            return OBJECT_MAPPER.writeValueAsString(target);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public FileDetails parse(String stringValue) {
        try {
            return OBJECT_MAPPER.readValue(stringValue, FileDetails.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
