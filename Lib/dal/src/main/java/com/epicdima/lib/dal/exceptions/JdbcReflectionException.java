package com.epicdima.lib.dal.exceptions;

/**
 * @author EpicDima
 */
public final class JdbcReflectionException extends RuntimeException {
    public JdbcReflectionException(String message) {
        super(message);
    }

    public JdbcReflectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public JdbcReflectionException(Throwable cause) {
        super(cause);
    }
}
