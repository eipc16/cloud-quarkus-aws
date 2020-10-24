package org.pwr.domain.images;

import org.pwr.infrastructure.dynamodb.AttributeConverter;
import org.pwr.infrastructure.dynamodb.DynamoDBTable;
import org.pwr.infrastructure.dynamodb.convertes.LocalDateTimeConverter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@DynamoDBTable(name = "images")
public class ImageDynamoEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String imageName;

    private String shortDescription;

    @AttributeConverter(LocalDateTimeConverter.class)
    private LocalDateTime uploadetAt;
}
