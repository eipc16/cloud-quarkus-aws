package org.pwr.infrastructure.dynamodb;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

public interface DynamoDBTypeConverter<T> {

    String convert(T target);

    T parse(String string);

    default Class<?> getSourceType() {
        return this.getClass().getTypeParameters()[0].getClass();
    }

    @SuppressWarnings("unchecked")
    default Class<?> getTargetType() {
        return Arrays.stream(this.getClass().getGenericInterfaces())
                .map(interfaceClazz -> (ParameterizedType) interfaceClazz)
                .map(parameterizedType -> parameterizedType.getActualTypeArguments()[0])
                .map(type -> (Class<?>) type)
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Incorrect generic type"));
    }

    @SuppressWarnings("unchecked")
    default String asString(Object target) {
        return convert((T) target);
    }
}
