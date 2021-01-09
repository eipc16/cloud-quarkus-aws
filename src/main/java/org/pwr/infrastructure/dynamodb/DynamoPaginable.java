package org.pwr.infrastructure.dynamodb;

import com.fasterxml.jackson.annotation.JsonProperty;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import javax.ws.rs.QueryParam;
import java.util.Map;
import java.util.Optional;

public class DynamoPaginable {

    @JsonProperty(value = "limit", defaultValue = "10")
    @QueryParam("limit")
    private Integer pageSize;

    @JsonProperty("next")
    @QueryParam("next")
    private String startAt;

    public DynamoPaginable() {
    }

    public DynamoPaginable(Integer pageSize, Map<String, AttributeValue> startAt) {
        this.pageSize = pageSize;
        this.startAt = AttributeValueMapper.stringify(startAt);
    }

    public Integer getPageSize() {
        return Optional.ofNullable(pageSize).orElse(10);
    }

    public Map<String, AttributeValue> getStartAt() {
        if(startAt == null) {
            return null;
        }
        return AttributeValueMapper.fromStringMap(startAt);
    }
}
