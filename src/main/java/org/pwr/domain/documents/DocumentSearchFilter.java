package org.pwr.domain.documents;

import org.pwr.infrastructure.dynamodb.DynamoFilter;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import javax.ws.rs.QueryParam;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/*
    AWS SDK 2, API for DynamoDB to be exact , is just... not good. This class is a great representation, if you want
    to filter some results from QueryRequest / ScanRequest you have to send 3 separate parameters.
    Mappings for attributeNames, mappings for queried values (AttributeValue class...)
    and finally the filter expression. Wish it worked like Hibernate but it doesn't so here we go writing enormous amounts
    of unnecessary boilerplate.
 */
public class DocumentSearchFilter implements DynamoFilter {

    @QueryParam("ocrStatus")
    private List<ProcessingStatus> ocrStatuses;

    @QueryParam("translateStatus")
    private List<ProcessingStatus> translateStatuses;

    public DocumentSearchFilter() {
        // empty
    }

    private DocumentSearchFilter(Builder builder) {
        ocrStatuses = builder.ocrStatuses;
        translateStatuses = builder.translateStatuses;
    }

    @Override
    public Map<String, String> getExpressionAttributeNames() {
        Map<String, String> names = new HashMap<>();

        if(ocrStatuses != null && !ocrStatuses.isEmpty()) {
            names.put("#ocrStatus", "ocrStatus");
        }

        if(ocrStatuses != null && !translateStatuses.isEmpty()) {
            names.put("#translationStatus", "translationStatus");
        }
        return names;
    }

    @Override
    public Map<String, AttributeValue> getExpressionAttributeValues() {
        Map<String, AttributeValue> valueMap = new HashMap<>();

        if(ocrStatuses != null && !ocrStatuses.isEmpty()) {

            for(int i = 0; i < ocrStatuses.size(); i++) {
                valueMap.put(
                        MessageFormat.format(":ocr{0}", i),
                        AttributeValue.builder()
                                .s(ocrStatuses.get(i).name())
                                .build()
                );
            }
        }

        if(translateStatuses != null && !translateStatuses.isEmpty()) {
            for(int i = 0; i < translateStatuses.size(); i++) {
                valueMap.put(
                        MessageFormat.format(":translate{0}", i),
                        AttributeValue.builder()
                                .s(translateStatuses.get(i).name())
                                .build()
                );
            }
        }

        return valueMap;
    }

    @Override
    public String getCondition() {
        List<String> conditions = new ArrayList<>();

        if(ocrStatuses != null && !ocrStatuses.isEmpty()) {
            String ocrStatuesKeys = IntStream.of(ocrStatuses.size())
                    .map(i -> i - 1)
                    .mapToObj(String::valueOf)
                    .map(i -> MessageFormat.format(":ocr{0}", i))
                    .collect(Collectors.joining(", "));
            conditions.add(MessageFormat.format(
                    "#ocrStatus IN ({0})",
                    ocrStatuesKeys
            ));
        }

        if(translateStatuses != null && !translateStatuses.isEmpty()) {
            String statusKeys = IntStream.of(translateStatuses.size())
                    .map(i -> i - 1)
                    .mapToObj(String::valueOf)
                    .map(i -> MessageFormat.format(":translate{0}", i))
                    .collect(Collectors.joining(", "));
            conditions.add(MessageFormat.format(
                    "#translationStatus IN ({0})",
                    statusKeys
            ));
        }

        return String.join(" AND ", conditions);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<ProcessingStatus> ocrStatuses = new ArrayList<>();
        private List<ProcessingStatus> translateStatuses = new ArrayList<>();

        public Builder withOCRStatuses(Collection<ProcessingStatus> ocrStatuses) {
            this.ocrStatuses.clear();
            this.ocrStatuses.addAll(ocrStatuses);
            return this;
        }

        public Builder withTranslateStatuses(Collection<ProcessingStatus> translateStatuses) {
            this.translateStatuses.clear();
            this.translateStatuses.addAll(translateStatuses);
            return this;
        }

        public DocumentSearchFilter build() {
            return new DocumentSearchFilter(this);
        }
    }
}
