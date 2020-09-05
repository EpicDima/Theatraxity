package com.epicdima.theatraxity.dal.di;

import com.epicdima.lib.di.annotations.Module;
import com.epicdima.theatraxity.dal.getters.*;

/**
 * @author EpicDima
 */
@Module(implementations = {
        GetAuthorById.class,
        GetGenreById.class,
        GetPlayById.class,
        GetPresentationById.class,
        GetUserById.class,
        GetOrderById.class
})
public final class GettersModule {
}
