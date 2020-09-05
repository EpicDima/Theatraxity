package com.epicdima.lib.controller;

import java.io.Reader;
import java.lang.reflect.Type;

/**
 * @author EpicDima
 */
public interface Converter {
    String getContentType();
    String to(Object object);
    <T> T from(String string, Type type);
    <T> T from(Reader reader, Type type);
}
