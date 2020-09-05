package com.epicdima.lib.di;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.epicdima.lib.di.AnnotationsHelper.getPostConstructMethod;

/**
 * @author EpicDima
 */
final class ReflectionUtils {
    private ReflectionUtils() {
        throw new AssertionError();
    }

    static void setValueToField(Object object, Field field, Object objectForSet) throws IllegalAccessException {
        field.setAccessible(true);
        field.set(object, objectForSet);
    }

    static List<Class<?>> getAllInterfacesAndSuperClasses(Class<?> cls) {
        List<Class<?>> interfaces = new ArrayList<>();
        while (!Object.class.equals(cls)) {
            interfaces.add(cls);
            interfaces.addAll(Arrays.asList(cls.getInterfaces()));
            cls = cls.getSuperclass();
        }
        return interfaces;
    }

    static void invokePostConstruct(Class<?> cls, Object object) throws ReflectiveOperationException {
        Method method = getPostConstructMethod(cls);
        if (method != null) {
            method.invoke(object);
        }
    }
}
