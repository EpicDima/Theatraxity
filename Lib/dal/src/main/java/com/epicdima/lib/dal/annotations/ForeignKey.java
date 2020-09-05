package com.epicdima.lib.dal.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author EpicDima
 */
@Target({ FIELD })
@Retention(RUNTIME)
public @interface ForeignKey {
    Class<?> entity();
    String parentColumn();
    boolean onDeleteCascade() default true;
}
