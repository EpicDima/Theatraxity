package com.epicdima.theatraxity.dal.mysql.builders.configurators;

import com.epicdima.lib.dal.annotations.handlers.ColumnAnnotationHandler;
import com.epicdima.lib.dal.other.ColumnConfigurator;
import com.epicdima.lib.dal.utils.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * @author EpicDima
 */
public final class MySqlTypeColumnConfigurator implements ColumnConfigurator {
    @Override
    public String configure(Field field, String previous) {
        switch (field.getType().getSimpleName()) {
            case "boolean":
                return previous + "BIT";
            case "int":
                return previous + "INT";
            case "double":
                return previous + "DOUBLE";
            case "String":
                ColumnAnnotationHandler handler = new ColumnAnnotationHandler();
                handler.handle(field);
                return String.format("%s VARCHAR(%d)", previous, handler.getLength());
            case "Date":
                return previous + "DATE";
            case "BigDecimal":
                return previous + "DECIMAL";
            default:
                if (ReflectionUtils.fieldIsEnum(field)) {
                    return String.format("%s ENUM(%s)", previous, ReflectionUtils.getEnumString(field.getType()));
                } else {
                    throw new RuntimeException("Unknown type for casting");
                }
        }
    }
}