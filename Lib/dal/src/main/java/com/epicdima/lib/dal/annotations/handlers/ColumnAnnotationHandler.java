package com.epicdima.lib.dal.annotations.handlers;

import com.epicdima.lib.dal.annotations.Column;

import java.lang.reflect.Field;

/**
 * @author EpicDima
 */
public final class ColumnAnnotationHandler extends AnnotationHandler<Column> {

    private String name;
    private int length;
    private boolean notNull;

    @Override
    public boolean handle(Field field) {
        if (super.handle(field)) {
            name = annotation.name();
            if ("".equals(name)) {
                name = field.getName();
            }
            length = annotation.length();
            notNull = annotation.notNull();
            return true;
        }
        return false;
    }

    public String getName() {
        checkNotExistingAnnotationState();
        return name;
    }

    public int getLength() {
        checkNotExistingAnnotationState();
        return length;
    }

    public boolean isNotNull() {
        checkNotExistingAnnotationState();
        return notNull;
    }
}
