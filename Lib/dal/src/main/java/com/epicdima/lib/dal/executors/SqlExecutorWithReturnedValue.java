package com.epicdima.lib.dal.executors;

import java.sql.Connection;

/**
 * @author EpicDima
 */
public interface SqlExecutorWithReturnedValue<T> {
    T execute(Connection connection) throws Exception;
}
