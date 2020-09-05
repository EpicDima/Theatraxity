package com.epicdima.lib.di.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author EpicDima
 */
@Target({ FIELD, CONSTRUCTOR, METHOD })
@Retention(RUNTIME)
public @interface Inject {}