package org.pwr.infrastructure.dynamodb;

public interface DynamoDBTypeConverter<S, T> {

    S convert(T target);

    T parse(S source);

    default Class<?> getSourceType() {
        return this.getClass().getTypeParameters()[0].getClass();
    }

    default Class<?> getTargetType() {
        return this.getClass().getTypeParameters()[1].getClass();
    }
}
