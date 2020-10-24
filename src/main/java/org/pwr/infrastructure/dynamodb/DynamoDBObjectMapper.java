package org.pwr.infrastructure.dynamodb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import javax.enterprise.context.ApplicationScoped;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class DynamoDBObjectMapper {

    private static final Map<String, DynamoDBTypeConverter<?>> CONVERTES_BY_NAME = new ConcurrentHashMap<>();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    Map<String, AttributeValue> mapEntityToValuesByNames(Object object) {
        return Arrays.stream(object.getClass().getDeclaredFields())
                .collect(Collectors.toMap(Field::getName, field -> mapToAttributeValue(object, field)));
    }

    private AttributeValue mapToAttributeValue(Object object, Field field) {
        return getValueAsAttributeValue(object, field)
                .orElseGet(() -> AttributeValue.builder()
                        .nul(true)
                        .build());
    }

    private Optional<AttributeValue> getValueAsAttributeValue(Object object, Field field) {
        Class<?> declaredType = field.getType();
        if (String.class.equals(declaredType)) {
            return getValueFromField(object, field)
                    .map(val -> AttributeValue.builder()
                            .s(val.toString())
                            .build());
        } else if (Number.class.isAssignableFrom(declaredType)) {
            return getValueFromField(object, field)
                    .map(val -> AttributeValue.builder()
                            .n(val.toString())
                            .build());
        } else if (Collection.class.isAssignableFrom(declaredType)) {
            return getValueFromField(object, field)
                    .map(val -> AttributeValue.builder()
                            .ss(((Collection<?>) val).stream()
                                    .map(Object::toString)
                                    .collect(Collectors.toList()))
                            .build());
        } else if (Boolean.class.equals(declaredType)) {
            return getValueFromField(object, field)
                    .map(val -> AttributeValue.builder()
                            .bool(Boolean.parseBoolean(val.toString()))
                            .build());
        } else if (field.isAnnotationPresent(AttributeConverter.class)) {
            Class<? extends DynamoDBTypeConverter<?>> converterClass = field.getAnnotation(AttributeConverter.class).value();
            DynamoDBTypeConverter<?> converter = CONVERTES_BY_NAME.computeIfAbsent(converterClass.getName(), converterClassName -> getConverterInstance(converterClass));
            return getValueFromField(object, field)
                    .map(converter::asString)
                    .map(stringVal -> AttributeValue.builder()
                            .s(stringVal)
                            .build());
        }
        return getAsJsonString(object, field, declaredType)
                .map(stringVal -> AttributeValue.builder()
                        .s(stringVal)
                        .build());
    }

    private Optional<Object> getValueFromField(Object object, Field field) {
        try {
            field.setAccessible(true);
            return Optional.ofNullable(field.get(object));
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private Optional<String> getAsJsonString(Object object, Field field, Class<?> declaredType) {
        return Optional.ofNullable(getValueFromField(object, field))
                .map(x -> {
                    try {
                        return OBJECT_MAPPER.writeValueAsString(x);
                    } catch (JsonProcessingException e) {
                        throw new IllegalStateException(MessageFormat.format("Could not convert field: {0} of type: {1}", field.getName(), declaredType.getSimpleName()));
                    }
                });
    }

    private static DynamoDBTypeConverter<?> getConverterInstance(Class<? extends DynamoDBTypeConverter<?>> converterClass) {
        try {
            return converterClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException(MessageFormat.format("Could not create converter instance: {0}", converterClass.getSimpleName()));
        }
    }

    public <T> T mapToEntity(Class<T> targetClass, Map<String, AttributeValue> attributeValueMap) {
        try {
            T target = targetClass.getConstructor().newInstance();
            Arrays.stream(targetClass.getDeclaredFields())
                    .filter(field -> attributeValueMap.containsKey(field.getName()))
                    .forEach(field -> setFieldValue(target, field, attributeValueMap.get(field.getName())));
            return target;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException(MessageFormat.format("Could not create instance of class: {0}", targetClass.getSimpleName()));
        }
    }

    private <T> void setFieldValue(T target, Field field, AttributeValue attributeValue) {
        field.setAccessible(true);
        try {
            trySetFieldValue(target, field, attributeValue);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(MessageFormat.format(
                    "Could not set value {0} to field {1} of type {2}. Cause: {3}",
                    attributeValue.toString(), field.getName(), field.getType().getSimpleName(), e.getCause()));
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void trySetFieldValue(T target, Field field, AttributeValue attributeValue) throws IllegalAccessException {
        Class<?> declaredType = field.getType();
        field.setAccessible(true);
        if (attributeValue.nul() != null && attributeValue.nul()) {
            field.set(target, null);
        } else if (declaredType.equals(String.class)) {
            if (attributeValue.s() != null) {
                field.set(target, attributeValue.s());
            }
        } else if (field.isAnnotationPresent(AttributeConverter.class)) {
            if (attributeValue.s() != null) {
                Class<? extends DynamoDBTypeConverter<?>> converterClass = field.getAnnotation(AttributeConverter.class).value();
                DynamoDBTypeConverter<?> converter = CONVERTES_BY_NAME.computeIfAbsent(converterClass.getName(), converterClassName -> getConverterInstance(converterClass));
                field.set(target, converter.parse(attributeValue.s()));
            }
        } else if (declaredType.equals(Long.class)) {
            field.set(target, parse(attributeValue.n(), Long::parseLong));
        } else if (declaredType.equals(Boolean.class)) {
            if (attributeValue.bool() != null) {
                field.set(target, attributeValue.bool());
            }
        } else if (declaredType.equals(Character.class)) {
            if (attributeValue.s() != null && attributeValue.s().length() > 1) {
                field.set(target, attributeValue.s().charAt(0));
            }
        } else if (declaredType.equals(Short.class)) {
            field.set(target, parse(attributeValue.n(), Short::parseShort));
        } else if (declaredType.equals(Integer.class)) {
            field.set(target, parse(attributeValue.n(), Integer::parseInt));
        } else if (declaredType.equals(Float.class)) {
            field.set(target, parse(attributeValue.n(), Float::parseFloat));
        } else if (declaredType.equals(Double.class)) {
            field.set(target, parse(attributeValue.n(), Double::parseDouble));
        } else if (Collection.class.isAssignableFrom(declaredType)) {
            Function<String, Object> mappingFunction = getMappingFunction(field);
            Collection<?> collection = Optional.ofNullable(attributeValue.ss())
                    .map(Collection::stream)
                    .map(vals -> vals.map(mappingFunction)
                            .collect(Collectors.toList()))
                    .orElseGet(Collections::emptyList);
            field.set(target, collection);
        } else {
            throw new IllegalAccessException(MessageFormat.format(
                    "Could not parse {0} to value of field {1} of type {2",
                    attributeValue.toString(), field.getName(), declaredType.getSimpleName()));
        }
    }

    private Function<String, Object> getMappingFunction(Field field) {
        if(field.isAnnotationPresent(AttributeConverter.class)) {
            Class<? extends DynamoDBTypeConverter<?>> converterClass = field.getAnnotation(AttributeConverter.class).value();
            DynamoDBTypeConverter<?> converter = CONVERTES_BY_NAME.computeIfAbsent(converterClass.getName(), converterClassName -> getConverterInstance(converterClass));
            return converter::parse;
        }
        Class<?> targetType = ((Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]);
        return stringValue -> parseToObject(targetType, stringValue);
    }

    private Object parseToObject(Class<?> targetClass, String stringValue) {
        if (String.class.equals(targetClass)) {
            return stringValue;
        } else if (Long.class.equals(targetClass)) {
            return parse(stringValue, Long::parseLong);
        } else if (Double.class.equals(targetClass)) {
            return parse(stringValue, Double::parseDouble);
        } else if (Float.class.equals(targetClass)) {
            return parse(stringValue, Float::parseFloat);
        } else if (Short.class.equals(targetClass)) {
            return parse(stringValue, Short::parseShort);
        } else if (Integer.class.equals(targetClass)) {
            return parse(stringValue, Integer::parseInt);
        } else if (Boolean.class.equals(targetClass)) {
            return parse(stringValue, Boolean::parseBoolean);
        } else if (Character.class.equals(targetClass)) {
            if (stringValue == null || stringValue.length() < 1) {
                return null;
            }
            return stringValue.charAt(0);
        } else {
            throw new IllegalStateException(MessageFormat.format(
                    "Could not parse {0} to type {1}",
                    stringValue, targetClass.getSimpleName()));
        }
    }

    private <T> T parse(String stringValue, Function<String, T> mapFunction) {
        if (stringValue == null) {
            return null;
        }
        return mapFunction.apply(stringValue);
    }

    public static Class<?> mapToSourceType(Class<? extends DynamoDBTypeConverter<?>> converterClass) {
        DynamoDBTypeConverter<?> converter = CONVERTES_BY_NAME.computeIfAbsent(converterClass.getName(), converterClassName -> getConverterInstance(converterClass));
        return converter.getSourceType();
    }
}
