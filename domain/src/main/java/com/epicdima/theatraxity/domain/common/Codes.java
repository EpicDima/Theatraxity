package com.epicdima.theatraxity.domain.common;

/**
 * @author EpicDima
 */
public final class Codes {
    private Codes() {
        throw new AssertionError();
    }

    public static final int UNEXPECTED_ERROR = 666;
    public static final int NOT_ALLOWED = 999;

    public static final int LOCATION_COST_ERROR = 123;

    public static final int ORDER_NOT_FOUND = 5;
    public static final int ORDER_IS_DELETED = 6;
    public static final int ORDER_IS_PAID = 7;
    public static final int ORDER_IS_CANCELLED = 8;
    public static final int USER_IS_ADMIN_OR_MANAGER = 9;
    public static final int NOT_FOUND_USER_BUYER = 10;
    public static final int USER_NOT_CLIENT = 11;
    public static final int SEATS_LIST_IS_EMPTY = 12;
    public static final int SEATS_ALREADY_OCCUPIED = 14;
    public static final int ORDER_DELIVERY_NOT_CONFIRMED = 17;
    public static final int ORDER_NOT_CANCELLED = 18;

    public static final int AUTHOR_NOT_FOUND = 15;
    public static final int NO_AUTHOR_CHANGE = 16;
    public static final int AUTHOR_IS_DELETED = 19;
    public static final int AUTHOR_NOT_DELETED = 20;
    public static final int AUTHOR_IS_NOT_DELETED = 21;
    public static final int AUTHOR_NOT_RESTORED = 22;

    public static final int GENRE_NOT_FOUND = 23;
    public static final int NO_GENRE_CHANGE = 24;
    public static final int GENRE_IS_DELETED = 25;
    public static final int GENRE_NOT_DELETED = 26;
    public static final int GENRE_IS_NOT_DELETED = 27;
    public static final int GENRE_NOT_RESTORED = 28;

    public static final int PLAY_NOT_FOUND = 29;
    public static final int NO_PLAY_CHANGE = 30;
    public static final int PLAY_IS_DELETED = 31;
    public static final int PLAY_NOT_DELETED = 32;
    public static final int PLAY_IS_NOT_DELETED = 33;
    public static final int PLAY_NOT_RESTORED = 34;

    public static final int PRESENTATION_NOT_FOUND = 35;
    public static final int NO_PRESENTATION_CHANGE = 36;
    public static final int PRESENTATION_IS_DELETED = 13;
    public static final int PRESENTATION_NOT_DELETED = 38;
    public static final int PRESENTATION_IS_NOT_DELETED = 39;
    public static final int PRESENTATION_NOT_RESTORED = 40;
    public static final int PRESENTATION_DATE_NOT_SPECIFIED = 41;
    public static final int PRESENTATION_DATE_BEFORE_CURRENT_DAY = 42;
    public static final int DATE_ALREADY_CONTAINS_PRESENTATION = 43;

    public static final int USER_NOT_FOUND = 44;
    public static final int NO_USER_CHANGE = 45;
    public static final int USER_NOT_DELETED = 46;
    public static final int USER_IS_NOT_DELETED = 47;
    public static final int USER_NOT_RESTORED = 48;
    public static final int NOT_VALID_PASSWORD = 49;
    public static final int NOT_VALID_EMAIL = 50;
    public static final int NOT_UNIQUE_EMAIL = 51;
    public static final int NEED_PASSWORD = 52;
    public static final int USER_IS_DELETED = 53;
    public static final int INCORRECT_EMAIL_OR_PASSWORD = 54;
    public static final int USER_ROLE_NOT_SPECIFIED = 56;
    public static final int INCORRECT_PASSWORD = 57;

    public static final int NOT_VALID_AUTHOR_NAME = 58;
    public static final int NOT_VALID_GENRE_NAME = 59;
    public static final int NOT_VALID_PLAY_NAME = 60;
    public static final int NOT_VALID_PLAY_DESCRIPTION = 61;
}
