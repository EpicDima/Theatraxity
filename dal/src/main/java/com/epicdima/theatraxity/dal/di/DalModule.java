package com.epicdima.theatraxity.dal.di;

import com.epicdima.lib.di.annotations.Module;

/**
 * @author EpicDima
 */
@Module(submodules = {
        DatabaseModule.class,
        DaoModule.class,
        GettersModule.class,
        MappersModule.class
})
public final class DalModule {
}
