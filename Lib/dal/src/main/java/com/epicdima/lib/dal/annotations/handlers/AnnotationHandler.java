package com.epicdima.lib.dal.annotations.handlers;

import com.epicdima.lib.dal.exceptions.AnnotationHandlerException;
import com.epicdima.lib.dal.utils.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author EpicDima
 */
public abstract class AnnotationHandler<T extends Annotation> {

    private final Class<T> annotationType = ReflectionUtils.getFirstGenericType(getClass());
    protected T annotation;

    protected boolean isExists(AnnotatedElement annotatedElement) {
        return annotatedElement.isAnnotationPresent(annotationType);
    }

    protected T getAnnotation(AnnotatedElement annotatedElement) {
        checkExistingAnnotationState();
        if (isExists(annotatedElement)) {
            annotation = annotatedElement.getAnnotation(annotationType);
        } else {
            annotation = null;
        }
        return annotation;
    }

    protected void checkExistingAnnotationState() {
        if (annotation != null) {
            throw new AnnotationHandlerException(String.format("Annotation '%s' already exists. AnnotationHandler can handle only one instance of the annotation", annotation));
        }
    }

    protected void checkNotExistingAnnotationState() {
        if (annotation == null) {
            throw new AnnotationHandlerException("Annotation not found. Processed object passed to the 'handle' method did not contain the necessary annotation or the 'handle' method was not called");
        }
    }

    protected boolean handle(Class<?> type) {
        return getAnnotation(type) != null;
    }

    protected boolean handle(Constructor<?> constructor) {
        return getAnnotation(constructor) != null;
    }

    protected boolean handle(Field field) {
        return getAnnotation(field) != null;
    }

    protected boolean handle(Method method) {
        return getAnnotation(method) != null;
    }

    public Class<T> getAnnotationType() {
        return annotationType;
    }
}
