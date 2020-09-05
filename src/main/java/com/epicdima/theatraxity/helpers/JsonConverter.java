package com.epicdima.theatraxity.helpers;

import com.epicdima.lib.controller.Converter;
import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.lib.di.annotations.Singleton;
import com.google.gson.Gson;

import java.io.Reader;
import java.lang.reflect.Type;

/**
 * @author EpicDima
 */
@Singleton
public final class JsonConverter implements Converter {

    private final Gson gson;

    @Inject
    public JsonConverter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public String getContentType() {
        return "application/json; charset=utf-8";
    }

    @Override
    public String to(Object object) {
        return gson.toJson(object);
    }

    @Override
    public <T> T from(String string, Type type) {
        return gson.fromJson(string, type);
    }

    @Override
    public <T> T from(Reader reader, Type type) {
        return gson.fromJson(reader, type);
    }
}
