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
public @interface Module {
    Class<?>[] implementations() default {};

    Class<?>[] submodules() default {};
}
