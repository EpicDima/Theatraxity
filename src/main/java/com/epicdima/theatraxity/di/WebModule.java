package com.epicdima.theatraxity.di;

import com.epicdima.lib.di.annotations.Module;
import com.epicdima.lib.di.annotations.Provides;
import com.epicdima.lib.di.annotations.Singleton;
import com.epicdima.theatraxity.helpers.DateFormat;
import com.epicdima.theatraxity.helpers.JsonConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author EpicDima
 */
@Module(implementations = {
        JsonConverter.class,
        DateFormat.class
}, submodules = {
        UseCaseModule.class,
        ServiceModule.class
})
public final class WebModule {

    @Singleton
    @Provides
    public <T> Gson provideGson(DateFormat dateFormat) {
        return new GsonBuilder()
                .setDateFormat(dateFormat.getPattern())
                .create();
    }
}
