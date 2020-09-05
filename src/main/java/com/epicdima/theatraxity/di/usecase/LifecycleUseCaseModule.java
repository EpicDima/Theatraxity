package com.epicdima.theatraxity.di.usecase;

import com.epicdima.lib.di.annotations.Module;
import com.epicdima.lib.di.annotations.Provides;
import com.epicdima.theatraxity.domain.dao.UserDao;
import com.epicdima.theatraxity.domain.usecases.lifecycle.ConfigureDatabaseOnStartUseCase;
import com.epicdima.theatraxity.domain.usecases.lifecycle.ConfigureLocationsOnStartUseCase;
import com.epicdima.theatraxity.domain.usecases.lifecycle.ConfigureLocationsOnStopUseCase;

/**
 * @author EpicDima
 */
@Module
public final class LifecycleUseCaseModule {
    @Provides
    public ConfigureDatabaseOnStartUseCase provideConfigureDatabaseOnStartUseCase(UserDao userDao) {
        return new ConfigureDatabaseOnStartUseCase(userDao);
    }

    @Provides
    public ConfigureLocationsOnStartUseCase provideConfigureLocationsOnStartUseCase() {
        return new ConfigureLocationsOnStartUseCase();
    }

    @Provides
    public ConfigureLocationsOnStopUseCase provideConfigureLocationsOnStopUseCase() {
        return new ConfigureLocationsOnStopUseCase();
    }
}
