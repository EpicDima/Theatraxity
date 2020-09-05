package com.epicdima.theatraxity.di;

import com.epicdima.lib.di.annotations.Module;
import com.epicdima.theatraxity.di.usecase.*;

/**
 * @author EpicDima
 */
@Module(submodules = {
        AuthorUseCaseModule.class,
        GenreUseCaseModule.class,
        LifecycleUseCaseModule.class,
        LocationUseCaseModule.class,
        OrderUseCaseModule.class,
        PlayUseCaseModule.class,
        PresentationUseCaseModule.class,
        UserUseCaseModule.class
})
public final class UseCaseModule {
}
