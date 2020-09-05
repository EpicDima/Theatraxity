package com.epicdima.theatraxity.dal.mysql;

import com.epicdima.lib.dal.utils.ConfigurationManager;
import com.epicdima.lib.di.annotations.Singleton;

/**
 * @author EpicDima
 */
@Singleton
public final class MySqlConfigurationManager extends ConfigurationManager {
    private static final String CONFIG_FILE = "mysql_config.properties";

    public MySqlConfigurationManager() {
        super(CONFIG_FILE);
    }
}
