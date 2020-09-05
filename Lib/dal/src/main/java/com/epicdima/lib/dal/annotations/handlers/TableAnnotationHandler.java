package com.epicdima.lib.dal.annotations.handlers;

import com.epicdima.lib.dal.annotations.Table;

/**
 * @author EpicDima
 */
public final class TableAnnotationHandler extends AnnotationHandler<Table> {

    private String name;

    @Override
    public boolean handle(Class<?> type) {
        if (super.handle(type)) {
            name = annotation.name();
            return true;
        }
        return false;
    }

    public String getName() {
        checkNotExistingAnnotationState();
        return name;
    }
}
