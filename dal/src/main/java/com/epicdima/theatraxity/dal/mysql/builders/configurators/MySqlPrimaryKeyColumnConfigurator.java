package com.epicdima.theatraxity.dal.mysql.builders.configurators;

import com.epicdima.lib.dal.annotations.handlers.PrimaryKeyAnnotationHandler;
import com.epicdima.lib.dal.other.ColumnConfigurator;

import java.lang.reflect.Field;

/**
 * @author EpicDima
 */
public final class MySqlPrimaryKeyColumnConfigurator implements ColumnConfigurator {
    @Override
    public String configure(Field field, String previous) {
        if (new PrimaryKeyAnnotationHandler().handle(field)) {
            return previous + "PRIMARY KEY";
        }
        return previous;
    }
}
