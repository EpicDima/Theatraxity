package com.epicdima.lib.dal.utils;

import com.epicdima.lib.dal.exceptions.JdbcReflectionException;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author EpicDima
 */
public final class ReflectionUtils {

    private ReflectionUtils() {
        throw new AssertionError();
    }

    public static <T> T createInstanceOfEntityClass(Class<T> entityClass) {
        try {
            Constructor<T> constructor = entityClass.getDeclaredConstructor();
            if (!Modifier.isPublic(constructor.getModifiers())) {
                throw new JdbcReflectionException(String.format("Constructor '%s' of entity class '%s' must be public", constructor, entityClass));
            }
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new JdbcReflectionException(String.format("Entity class '%s' must have default public constructor without arguments", entityClass), e);
        }
    }

    public static boolean fieldIsEnum(Field field) {
        return field.getType().isEnum();
    }

    public static void setValue(Object obj, Field field, Object value) {
        try {
            if (Modifier.isFinal(field.getModifiers())) {
                throw new JdbcReflectionException(String.format("Field '%s' of entity class cannot be final", field));
            }
            if (Modifier.isPublic(field.getModifiers())) {
                field.set(obj, value);
            } else {
                Method setter = getSetter(obj.getClass(), field.getName(), field.getType());
                if (setter == null) {
                    throw new NoSuchMethodException(String.format("Not found setter for field '%s'", field));
                }
                setter.invoke(obj, value);
            }
        } catch (ReflectiveOperationException e) {
            throw new JdbcReflectionException(e);
        }
    }

    private static Method getSetter(Class<?> entityClass, String fieldName, Class<?> valueClass)
            throws NoSuchMethodException {
        Method setter = entityClass.getDeclaredMethod(getMethodNameFromFieldName(fieldName, "set"), valueClass);
        if (Modifier.isPublic(setter.getModifiers())
                && setter.getReturnType().equals(void.class)
                && setter.getParameterCount() == 1) {
            return setter;
        }
        return null;
    }

    public static Object getValue(Object obj, Field field) {
        try {
            if (Modifier.isPublic(field.getModifiers())) {
                return field.get(obj);
            } else {
                Method getter = getGetter(obj.getClass(), field.getName());
                if (getter == null) {
                    throw new NoSuchMethodException(String.format("Not found getter for field '%s'", field));
                }
                return getter.invoke(obj);
            }
        } catch (ReflectiveOperationException e) {
            throw new JdbcReflectionException(e);
        }
    }

    private static Method getGetter(Class<?> entityClass, String fieldName) throws NoSuchMethodException {
        Method getter = entityClass.getDeclaredMethod(getMethodNameFromFieldName(fieldName, "get"));
        if (getter.getReturnType().equals(void.class)) {
            getter = entityClass.getDeclaredMethod(getMethodNameFromFieldName(fieldName, "is"));
            if (!getter.getReturnType().equals(boolean.class)) {
                getter = null;
            }
        }
        if (getter != null) {
            if (!Modifier.isPublic(getter.getModifiers()) || getter.getParameterCount() != 0) {
                getter = null;
            }
        }
        return getter;
    }

    private static String getMethodNameFromFieldName(String fieldName, String prefix) {
        return prefix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    public static <E> E getEnumConstant(Class<E> enumClass, String enumStringValue) {
        for (E enumConstant : enumClass.getEnumConstants()) {
            if (enumConstant.toString().equals(enumStringValue)) {
                return enumConstant;
            }
        }
        throw new JdbcReflectionException(String.format("Not found enum constant of enum class '%s' with name '%s'", enumClass, enumStringValue));
    }

    public static <E> String getEnumString(Class<E> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(Object::toString)
                .collect(Collectors.joining("\", \"", "\"", "\""));
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getFirstGenericType(Class<?> cls) {
        return (Class<T>) ((ParameterizedType) cls.getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
