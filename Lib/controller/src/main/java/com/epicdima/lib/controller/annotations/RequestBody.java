package com.epicdima.lib.controller.annotations;

import com.epicdima.lib.controller.Converter;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author EpicDima
 */
@Target({ PARAMETER })
@Retention(RUNTIME)
public @interface RequestBody {
    Class<? extends Converter> converter() default Converter.class;
}
