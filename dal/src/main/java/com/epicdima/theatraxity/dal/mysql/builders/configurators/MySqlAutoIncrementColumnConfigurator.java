package com.epicdima.theatraxity.dal.mysql.builders.configurators;

import com.epicdima.lib.dal.annotations.handlers.PrimaryKeyAnnotationHandler;
import com.epicdima.lib.dal.other.ColumnConfigurator;

import java.lang.reflect.Field;

/**
 * @author EpicDima
 */
public final class MySqlAutoIncrementColumnConfigurator implements ColumnConfigurator {
    @Override
    public String configure(Field field, String previous) {
        if (new PrimaryKeyAnnotationHandler().handle(field) && "int".equals(field.getType().toString())) {
            return previous + "AUTO_INCREMENT";
        }
        return previous;
    }
}