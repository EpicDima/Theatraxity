package com.epicdima.lib.dal.exceptions;

/**
 * @author EpicDima
 */
public final class ConnectionPoolException extends RuntimeException {

    public ConnectionPoolException(String message) {
        super(message);
    }

    public ConnectionPoolException(String message, Throwable cause) {
        super(message, cause);
    }
}
