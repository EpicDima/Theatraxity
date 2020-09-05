package com.epicdima.lib.di.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author EpicDima
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface Scan {
    String[] packages() default {};

    boolean recursively() default false;

    boolean inner() default true;
}
