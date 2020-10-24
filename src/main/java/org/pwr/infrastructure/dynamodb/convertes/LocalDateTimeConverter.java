package org.pwr.infrastructure.dynamodb.convertes;

import org.pwr.infrastructure.dynamodb.DynamoDBTypeConverter;

import java.time.LocalDateTime;

public class LocalDateTimeConverter implements DynamoDBTypeConverter<LocalDateTime> {

    @Override
    public String convert(LocalDateTime target) {
        return target.toString();
    }

    @Override
    public LocalDateTime parse(String source) {
        return LocalDateTime.parse(source);
    }
}
