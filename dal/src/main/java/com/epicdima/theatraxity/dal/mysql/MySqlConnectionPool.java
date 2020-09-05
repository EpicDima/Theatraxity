package com.epicdima.theatraxity.dal.mysql;


import com.epicdima.lib.dal.other.ConnectionPool;
import com.epicdima.lib.dal.other.DatabaseInitializer;
import com.epicdima.lib.dal.utils.ConfigurationManager;
import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.lib.di.annotations.Singleton;

/**
 * @author EpicDima
 */
@Singleton
public final class MySqlConnectionPool extends ConnectionPool {
    @Inject
    public MySqlConnectionPool(ConfigurationManager configurationManager,
                               DatabaseInitializer databaseInitializer) {
        super(configurationManager, databaseInitializer);
    }
}
