package com.epicdima.lib.dal.executors;

import java.sql.Connection;

/**
 * @author EpicDima
 */
public interface SqlExecutor {
    void execute(Connection connection);
}
