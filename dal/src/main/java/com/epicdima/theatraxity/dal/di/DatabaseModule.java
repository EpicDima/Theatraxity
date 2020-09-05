package com.epicdima.theatraxity.dal.di;

import com.epicdima.lib.di.annotations.Module;
import com.epicdima.theatraxity.dal.mysql.MySqlConfigurationManager;
import com.epicdima.theatraxity.dal.mysql.MySqlConnectionPool;
import com.epicdima.theatraxity.dal.mysql.MySqlDatabaseInitializer;

/**
 * @author EpicDima
 */
@Module(implementations = {
        MySqlDatabaseInitializer.class,
        MySqlConfigurationManager.class,
        MySqlConnectionPool.class
})
public final class DatabaseModule {
}
