package com.epicdima.theatraxity.helpers;

import com.epicdima.lib.di.annotations.Singleton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author EpicDima
 */
@Singleton
public final class DateFormat {
    private static final String PATTERN = "yyyy-MM-dd";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(PATTERN);

    public String getPattern() {
        return PATTERN;
    }

    public Date format(String source) {
        if (source == null) {
            return null;
        }
        try {
            return DATE_FORMAT.parse(source);
        } catch (ParseException e) {
            return null;
        }
    }
}
