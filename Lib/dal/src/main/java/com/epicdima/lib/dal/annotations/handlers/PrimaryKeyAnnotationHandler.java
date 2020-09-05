package com.epicdima.lib.dal.annotations.handlers;

import com.epicdima.lib.dal.annotations.PrimaryKey;

import java.lang.reflect.Field;

/**
 * @author EpicDima
 */
public final class PrimaryKeyAnnotationHandler extends AnnotationHandler<PrimaryKey> {
    @Override
    public boolean handle(Field field) {
        return super.handle(field);
    }
}
