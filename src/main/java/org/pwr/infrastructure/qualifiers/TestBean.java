package org.pwr.infrastructure.qualifiers;

import javax.inject.Qualifier;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Qualifier
public @interface TestBean {
}