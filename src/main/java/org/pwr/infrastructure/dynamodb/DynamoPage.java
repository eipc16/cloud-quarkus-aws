package org.pwr.infrastructure.dynamodb;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@JsonSerialize
public class DynamoPage<T> {

    @JsonProperty
    private Collection<T> content;

    @JsonProperty("limit")
    private Integer limit;

    private Map<String, AttributeValue> start;
    private Map<String, AttributeValue> next;

    public DynamoPage() {

    }

    public DynamoPage(Collection<T> content, Integer limit, Map<String, AttributeValue> start, Map<String, AttributeValue> next) {
        this.content = Optional.ofNullable(content).orElseGet(Collections::emptyList);
        this.limit = Optional.ofNullable(limit).orElse(0);
        this.start = start;
        this.next = next;
    }

    @JsonGetter("pageCount")
    public Integer getPageCount() {
        return content.size();
    }

    @JsonGetter("start")
    public String getStartAsString() {
        if(start == null) {
            return null;
        }
        return AttributeValueMapper.stringify(start);
    }

    @JsonGetter("next")
    public String getNextAsString() {
        if(next == null) {
            return null;
        }
        return AttributeValueMapper.stringify(next);
    }

    public List<T> getContent() {
        return new ArrayList<>(content);
    }

    public Integer getLimit() {
        return limit;
    }

    public Map<String, AttributeValue> getStart() {
        return start;
    }

    public Map<String, AttributeValue> getNext() {
        return next;
    }

    public <R> DynamoPage<R> mapTo(Function<T, R> mapper) {
        if(content == null) {
            return new DynamoPage<>(null, limit, getStart(), getNext());
        }
        List<R> mappedValues = content.stream()
                .filter(Objects::nonNull)
                .map(mapper)
                .collect(Collectors.toUnmodifiableList());
        return new DynamoPage<>(mappedValues, limit, getStart(), getNext());
    }
}
