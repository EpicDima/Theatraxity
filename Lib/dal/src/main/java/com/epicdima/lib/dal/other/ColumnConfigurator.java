package com.epicdima.lib.dal.other;

import java.lang.reflect.Field;

/**
 * @author EpicDima
 */
public interface ColumnConfigurator {
    String configure(Field field, String previous);
}