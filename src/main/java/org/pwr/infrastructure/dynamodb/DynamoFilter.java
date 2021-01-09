package org.pwr.infrastructure.dynamodb;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public interface DynamoFilter {

    Map<String, String> getExpressionAttributeNames();

    Map<String, AttributeValue> getExpressionAttributeValues();

    String getCondition();
}
