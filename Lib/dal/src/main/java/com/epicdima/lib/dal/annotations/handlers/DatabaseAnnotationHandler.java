package com.epicdima.lib.dal.annotations.handlers;

import com.epicdima.lib.dal.annotations.Database;

/**
 * @author EpicDima
 */
public final class DatabaseAnnotationHandler extends AnnotationHandler<Database> {

    private Class<?>[] entities;

    @Override
    public boolean handle(Class<?> type) {
        if (super.handle(type)) {
            entities = annotation.entities();
            return true;
        }
        return false;
    }

    public Class<?>[] getEntities() {
        checkNotExistingAnnotationState();
        return entities;
    }
}
