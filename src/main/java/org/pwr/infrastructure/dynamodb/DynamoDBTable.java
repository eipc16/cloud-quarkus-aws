package org.pwr.infrastructure.dynamodb;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DynamoDBTable {
    String name();

    long readCapacity() default 6L;

    long writeCapacity() default 5L;

    boolean forceRebuild() default false;
}
