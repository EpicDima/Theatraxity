package com.epicdima.lib.dal.exceptions;

import java.sql.SQLException;

/**
 * @author EpicDima
 */
public final class ExecutorSqlException extends RuntimeException {

    public ExecutorSqlException(String message, Throwable cause) {
        super(message, cause);
    }
}
