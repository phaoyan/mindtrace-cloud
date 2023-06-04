package pers.juumii.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 具体的值在mindtrace-data的constants中
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ResourceType {
    String value();
}
