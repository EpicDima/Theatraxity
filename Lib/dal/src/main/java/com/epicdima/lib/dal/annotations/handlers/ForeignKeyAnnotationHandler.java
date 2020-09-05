package com.epicdima.lib.dal.annotations.handlers;

import com.epicdima.lib.dal.annotations.ForeignKey;

import java.lang.reflect.Field;

/**
 * @author EpicDima
 */
public final class ForeignKeyAnnotationHandler extends AnnotationHandler<ForeignKey> {

    private Class<?> entity;
    private String parentColumn;
    private boolean onDeleteCascade;

    @Override
    public boolean handle(Field field) {
        if (super.handle(field)) {
            entity = annotation.entity();
            parentColumn = annotation.parentColumn();
            onDeleteCascade = annotation.onDeleteCascade();
            return true;
        }
        return false;
    }

    public Class<?> getEntity() {
        checkNotExistingAnnotationState();
        return entity;
    }

    public String getParentColumn() {
        checkNotExistingAnnotationState();
        return parentColumn;
    }

    public boolean isOnDeleteCascade() {
        checkNotExistingAnnotationState();
        return onDeleteCascade;
    }
}
