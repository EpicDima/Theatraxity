package com.epicdima.theatraxity.helpers;

/**
 * @author EpicDima
 */
public final class Utils {
    private Utils() {
        throw new AssertionError();
    }

    public static <T extends Enum<T>> T valueOfEnum(Class<T> enumType, String name) {
        try {
            return T.valueOf(enumType, name);
        } catch (NullPointerException | IllegalArgumentException e) {
            return null;
        }
    }
}
