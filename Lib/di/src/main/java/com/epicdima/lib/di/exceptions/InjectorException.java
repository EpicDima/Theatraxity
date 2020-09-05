package com.epicdima.lib.di.exceptions;

/**
 * @author EpicDima
 */
public class InjectorException extends RuntimeException {

    public InjectorException(String message) {
        super(message);
    }

    public InjectorException(Throwable cause) {
        super(cause);
    }
}
