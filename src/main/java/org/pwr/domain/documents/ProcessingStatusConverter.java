package org.pwr.domain.documents;

import org.pwr.infrastructure.dynamodb.DynamoDBTypeConverter;

public class ProcessingStatusConverter implements DynamoDBTypeConverter<ProcessingStatus> {

    @Override
    public String convert(ProcessingStatus target) {
        if(target == null) {
            return null;
        }
        return target.name();
    }

    @Override
    public ProcessingStatus parse(String string) {
        if(string == null) {
            return null;
        }
        return ProcessingStatus.valueOf(string);
    }
}
