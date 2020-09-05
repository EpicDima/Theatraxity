package com.epicdima.lib.controller.annotations;

import com.epicdima.lib.controller.Converter;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author EpicDima
 */
@Target({ METHOD })
@Retention(RUNTIME)
public @interface ResponseBody {
    Class<? extends Converter> converter() default Converter.class;
}
