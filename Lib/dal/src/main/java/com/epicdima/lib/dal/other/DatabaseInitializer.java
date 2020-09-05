package com.epicdima.lib.dal.other;

import java.sql.Connection;

/**
 * @author EpicDima
 */
public interface DatabaseInitializer {
    void createDatabase(Connection connection, String databaseName);
    void createTables(Connection connection);
}
