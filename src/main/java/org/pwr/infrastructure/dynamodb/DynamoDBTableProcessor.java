package org.pwr.infrastructure.dynamodb;

import eu.infomas.annotation.AnnotationDetector;
import io.quarkus.runtime.StartupEvent;
import org.jboss.logging.Logger;
import software.amazon.awssdk.services.dynamodb.model.*;

import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.Id;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Startup
@ApplicationScoped
class DynamoDBTableProcessor {

    private static final Logger LOGGER = Logger.getLogger(DynamoDBTableProcessor.class);

    private static AnnotationDetector ANNOTATION_DETECTOR;

    private final DynamoDBService dynamoDBService;

    @Inject
    DynamoDBTableProcessor(DynamoDBService dynamoDBService) {
        this.dynamoDBService = dynamoDBService;
    }

    void onStart(@Observes StartupEvent event) {
        try {
            LOGGER.info("Starting entity scan...");
            if (ANNOTATION_DETECTOR == null) {
                ANNOTATION_DETECTOR = getDetector(dynamoDBService);
                LOGGER.info("Initialized AnnotationDetector");
            }
            ANNOTATION_DETECTOR.detect();
        } catch (IOException e) {
            LOGGER.error("Could not scan files for annotation...");
            e.printStackTrace();
        }
    }

    private static TableDefinition getTableDefinition(Class<?> clazz, DynamoDBTable tableAnnotation) {
        return TableDefinition.builder(tableAnnotation.name())
                .withKeys(getKeys(clazz))
                .withAttributes(getAttributes(clazz))
                .withTroughput(ProvisionedThroughput.builder()
                        .readCapacityUnits(tableAnnotation.readCapacity())
                        .writeCapacityUnits(tableAnnotation.writeCapacity())
                        .build())
                .withForceRebuild(tableAnnotation.forceRebuild())
                .build();
    }

    private static List<KeySchemaElement> getKeys(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .map(DynamoDBTableProcessor::getKeyFromField)
                .collect(Collectors.toList());
    }

    private static KeySchemaElement getKeyFromField(Field field) {
        return KeySchemaElement.builder()
                .attributeName(field.getName())
                .keyType(KeyType.HASH)
                .build();
    }

    private static List<AttributeDefinition> getAttributes(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .map(DynamoDBTableProcessor::getAttributeFromField)
                .collect(Collectors.toList());
    }

    private static AttributeDefinition getAttributeFromField(Field field) {
        return AttributeDefinition.builder()
                .attributeName(field.getName())
                .attributeType(getTypeFromField(field))
                .build();
    }

    private static ScalarAttributeType getTypeFromField(Field field) {
        if (!field.isAnnotationPresent(AttributeConverter.class)) {
            return getTypeFromClass(field.getType());
        }
        return getTypeFromClass(getSourceType(field));
    }

    @SuppressWarnings("unchecked")
    private static Class<?> getSourceType(Field field) {
        return Arrays.stream(field.getAnnotation(AttributeConverter.class).value().getGenericInterfaces())
                .map(interfaceClazz -> (ParameterizedType) interfaceClazz)
                .map(parameterizedType -> parameterizedType.getActualTypeArguments()[0])
                .map(type -> (Class<?>) type)
                .findAny()
                .orElseThrow(() -> new IllegalStateException(MessageFormat.format("Invalid converter for field: {0}", field.getName())));
    }

    private static ScalarAttributeType getTypeFromClass(Class<?> clazz) {
        if (Number.class.isAssignableFrom(clazz)) {
            return ScalarAttributeType.N;
        } else if (clazz.equals(String.class)) {
            return ScalarAttributeType.S;
        } else {
            return ScalarAttributeType.B;
        }
    }

    private static AnnotationDetector getDetector(DynamoDBService dynamoDBService) {
        return new AnnotationDetector(new AnnotationDetector.TypeReporter() {
            @Override
            public void reportTypeAnnotation(Class<? extends Annotation> annotation, String className) {
                LOGGER.infof("Found Entity annotated with DynamoDBTable annotation... Class: %s", className);
                getClassFromName(className).ifPresent(this::createTable);
            }

            private void createTable(Class<?> clazz) {
                DynamoDBTable tableAnnotation = clazz.getAnnotation(DynamoDBTable.class);
                TableDefinition definition = getTableDefinition(clazz, tableAnnotation);
                LOGGER.infof("Entity: %s", definition);
                dynamoDBService.createTable(definition);
            }

            private Optional<Class<?>> getClassFromName(String className) {
                try {
                    return Optional.of(Class.forName(className));
                } catch (ClassNotFoundException e) {
                    LOGGER.errorf("Could not load class with name %s", className);
                }
                return Optional.empty();
            }

            @Override
            @SuppressWarnings("unchecked")
            public Class<? extends Annotation>[] annotations() {
                LOGGER.infof("Start scanning for anotation... %s", DynamoDBTable.class.getSimpleName());
                return new Class[]{DynamoDBTable.class};
            }
        });
    }
}
