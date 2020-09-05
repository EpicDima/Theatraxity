package com.epicdima.theatraxity.di;

import com.epicdima.lib.di.annotations.Module;
import com.epicdima.theatraxity.services.*;

/**
 * @author EpicDima
 */
@Module(implementations = {
        AuthorService.class,
        GenreService.class,
        LifecycleService.class,
        LocationService.class,
        OrderService.class,
        PlayService.class,
        PresentationService.class,
        UserService.class,
        VerifyUserService.class
})
public final class ServiceModule {
}
