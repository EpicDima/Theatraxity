package com.epicdima.lib.di.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author EpicDima
 */
@Target({ TYPE, FIELD, METHOD, PARAMETER })
@Retention(RUNTIME)
public @interface Named {
    String DEFAULT_NAME = "";

    String value() default DEFAULT_NAME;
}