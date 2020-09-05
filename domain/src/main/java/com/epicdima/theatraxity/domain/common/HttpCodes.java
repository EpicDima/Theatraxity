package com.epicdima.theatraxity.domain.common;

/**
 * @author EpicDima
 */
public final class HttpCodes {
    private HttpCodes() {
        throw new AssertionError();
    }

    public static final int OK = 200;

    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int SERVER_ERROR = 500;
}
