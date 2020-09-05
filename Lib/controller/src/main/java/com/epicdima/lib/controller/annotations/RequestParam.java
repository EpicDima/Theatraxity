package com.epicdima.lib.controller.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author EpicDima
 */
@Target({ PARAMETER })
@Retention(RUNTIME)
public @interface RequestParam {

    String value();

    String defaultValue() default "";

    boolean nullable() default false;
}
