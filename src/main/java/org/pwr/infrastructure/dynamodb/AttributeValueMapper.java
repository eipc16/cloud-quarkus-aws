package org.pwr.infrastructure.dynamodb;

import software.amazon.awssdk.core.BytesWrapper;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AttributeValueMapper {

    private static final String DELIMITER = "_";
    private static final String LIST_DELIMITER = "\\,/";
    private static final String MAP_DELIMITER = "=";
    private static final String NULL = "@@NULL@@";

    public static String stringify(AttributeValue attributeValue) {
        if (attributeValue == null || (attributeValue.nul() != null && attributeValue.nul())) {
            return null;
        }

        String stringifiedValue;

        if (attributeValue.s() != null) {
            stringifiedValue = attributeValue.s() + DELIMITER + "s";
        } else if (attributeValue.n() != null) {
            stringifiedValue = attributeValue.n() + DELIMITER + "n";
        } else if (attributeValue.b() != null) {
            stringifiedValue = attributeValue.b().asUtf8String() + DELIMITER + "b";
        } else if (attributeValue.hasSs()) {
            stringifiedValue = String.join(LIST_DELIMITER, attributeValue.ss()) + DELIMITER + "ss";
        } else if (attributeValue.hasNs()) {
            stringifiedValue = String.join(LIST_DELIMITER, attributeValue.ns()) + DELIMITER + "ns";
        } else if (attributeValue.hasBs()) {
            List<String> parts = attributeValue.bs().stream()
                    .map(BytesWrapper::asUtf8String)
                    .collect(Collectors.toUnmodifiableList());
            stringifiedValue = String.join(LIST_DELIMITER, parts) + DELIMITER + "bs";
        } else if (attributeValue.hasM()) {
            List<String> keyValMerged = attributeValue.m().entrySet().stream()
                    .map(x -> Optional.ofNullable(x.getKey()).orElse(null) +
                            MAP_DELIMITER +
                            Optional.ofNullable(x.getValue()).map(AttributeValueMapper::stringify).orElse(NULL))
                    .collect(Collectors.toUnmodifiableList());
            stringifiedValue = String.join(LIST_DELIMITER, keyValMerged) + DELIMITER + "M";
        } else if (attributeValue.hasL()) {
            List<String> mappedVals = attributeValue.l().stream()
                    .map(AttributeValueMapper::stringify)
                    .collect(Collectors.toUnmodifiableList());
            stringifiedValue = String.join(LIST_DELIMITER, mappedVals) + DELIMITER + "L";
        } else if (attributeValue.bool() != null) {
            stringifiedValue = attributeValue.bool() + DELIMITER + "bool";
        } else {
            stringifiedValue = null;
        }

        if (stringifiedValue == null) {
            return null;
        }

        return "[" + stringifiedValue + "]";
    }

    public static AttributeValue fromString(String stringifiedValue) {
        if (stringifiedValue == null || stringifiedValue.length() < 3 || !stringifiedValue.startsWith("[") || !stringifiedValue.endsWith("]")) {
            return AttributeValue.builder().nul(true).build();
        }

        String encodedVal = stringifiedValue.substring(1, stringifiedValue.length() - 1);
        int delimiterIndex = encodedVal.lastIndexOf(DELIMITER);

        if (delimiterIndex == -1) {
            return AttributeValue.builder().nul(true).build();
        }

        String value = encodedVal.substring(0, delimiterIndex);
        String key = encodedVal.substring(delimiterIndex + 1);

        switch (key) {
            case "s":
                return AttributeValue.builder()
                        .s(value)
                        .build();
            case "n":
                return AttributeValue.builder()
                        .n(value)
                        .build();
            case "b":
                return AttributeValue.builder()
                        .b(SdkBytes.fromUtf8String(value))
                        .build();
            case "ss":
                List<String> splittedValue = Arrays.asList(value.split(LIST_DELIMITER));
                return AttributeValue.builder()
                        .ss(splittedValue)
                        .build();
            case "ns":
                List<String> splittedNValue = Arrays.asList(value.split(LIST_DELIMITER));
                return AttributeValue.builder()
                        .ss(splittedNValue)
                        .build();
            case "bs":
                List<SdkBytes> bytesList = Arrays.stream(value.split(LIST_DELIMITER))
                        .map(SdkBytes::fromUtf8String)
                        .collect(Collectors.toUnmodifiableList());
                return AttributeValue.builder()
                        .bs(bytesList)
                        .build();
            case "M":
                Map<String, AttributeValue> valueMap = Arrays.stream(value.split(LIST_DELIMITER))
                        .map(keyMapVal -> keyMapVal.split(MAP_DELIMITER))
                        .collect(Collectors.toMap(
                                x -> NULL.equals(x[0]) ? null : x[0],
                                x -> NULL.equals(x[1]) ? AttributeValue.builder().nul(true).build() : fromString(x[1])
                        ));
                return AttributeValue.builder()
                        .m(valueMap)
                        .build();
            case "L":
                List<AttributeValue> valueList = Arrays.stream(value.split(LIST_DELIMITER))
                        .map(AttributeValueMapper::fromString)
                        .collect(Collectors.toUnmodifiableList());
                return AttributeValue.builder()
                        .l(valueList)
                        .build();
            case "bool":
                return AttributeValue.builder()
                        .bool(Boolean.parseBoolean(value))
                        .build();
        }

        return AttributeValue.builder().nul(true).build();
    }

    public static String stringify(Map<String, AttributeValue> attributeValueMap) {
        if (attributeValueMap == null) {
            return null;
        }

        List<String> keyValMerged = attributeValueMap.entrySet().stream()
                .map(x -> Optional.ofNullable(x.getKey()).orElse(null) +
                        MAP_DELIMITER +
                        Optional.ofNullable(x.getValue()).map(AttributeValueMapper::stringify).orElse(NULL))
                .collect(Collectors.toUnmodifiableList());
        return String.join(LIST_DELIMITER, keyValMerged);
    }

    public static Map<String, AttributeValue> fromStringMap(String stringifiedMap) {
        if (stringifiedMap == null || stringifiedMap.isEmpty()) {
            return null;
        }

        return Arrays.stream(stringifiedMap.split(LIST_DELIMITER))
                .map(value -> value.split(MAP_DELIMITER))
                .collect(Collectors.toMap(
                        x -> NULL.equals(x[0]) ? null : x[0],
                        x -> NULL.equals(x[1]) ? AttributeValue.builder().nul(true).build() : fromString(x[1])
                ));
    }
}
