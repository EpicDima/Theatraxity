package com.epicdima.theatraxity.dal.mysql.builders.configurators;

import com.epicdima.lib.dal.annotations.handlers.ColumnAnnotationHandler;
import com.epicdima.lib.dal.other.ColumnConfigurator;

import java.lang.reflect.Field;

/**
 * @author EpicDima
 */
public final class MySqlNameColumnConfigurator implements ColumnConfigurator {
    @Override
    public String configure(Field field, String previous) {
        ColumnAnnotationHandler handler = new ColumnAnnotationHandler();
        handler.handle(field);
        return previous + "`" + handler.getName() + "`";
    }
}