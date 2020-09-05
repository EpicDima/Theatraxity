package com.epicdima.theatraxity.services;

import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.lib.di.annotations.Singleton;
import com.epicdima.theatraxity.domain.usecases.lifecycle.ConfigureDatabaseOnStartUseCase;
import com.epicdima.theatraxity.domain.usecases.lifecycle.ConfigureLocationsOnStartUseCase;
import com.epicdima.theatraxity.domain.usecases.lifecycle.ConfigureLocationsOnStopUseCase;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * @author EpicDima
 */
@Singleton
public final class LifecycleService {

    private static final String THEATRE_LOCATIONS_CONFIG = "theatre_locations_config.xml";

    private final ConfigureDatabaseOnStartUseCase configureDatabaseOnStartUseCase;
    private final ConfigureLocationsOnStartUseCase configureLocationsOnStartUseCase;
    private final ConfigureLocationsOnStopUseCase configureLocationsOnStopUseCase;

    @Inject
    public LifecycleService(ConfigureDatabaseOnStartUseCase configureDatabaseOnStartUseCase,
                            ConfigureLocationsOnStartUseCase configureLocationsOnStartUseCase,
                            ConfigureLocationsOnStopUseCase configureLocationsOnStopUseCase) {
        this.configureDatabaseOnStartUseCase = configureDatabaseOnStartUseCase;
        this.configureLocationsOnStartUseCase = configureLocationsOnStartUseCase;
        this.configureLocationsOnStopUseCase = configureLocationsOnStopUseCase;
    }

    public void onStart() {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(THEATRE_LOCATIONS_CONFIG)) {
            configureLocationsOnStartUseCase.execute(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        configureDatabaseOnStartUseCase.execute();
    }

    public void onStop() {

        try (OutputStream stream = new FileOutputStream(Objects.requireNonNull(getClass().getClassLoader()
                .getResource(THEATRE_LOCATIONS_CONFIG)).getPath())) {
            configureLocationsOnStopUseCase.execute(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
