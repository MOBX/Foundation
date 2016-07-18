package com.lamfire.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public class DateFormatUtils {

    private static final Map<String, DateFormat> formators = Maps.newHashMap();

    public static String format(long millis, String pattern) {
        return format(new Date(millis), pattern);
    }

    public static String format(long millis, String pattern, TimeZone zone) {
        return format(new Date(millis), pattern, zone);
    }

    public synchronized static String format(Date date, String pattern, TimeZone zone) {
        DateFormat format = getDateFormat(pattern);
        format.setTimeZone(zone);
        return format.format(date);
    }

    public static String format(Date date, String pattern) {
        DateFormat format = getDateFormat(pattern);
        return format.format(date);
    }

    private static DateFormat getDateFormat(String pattern) {
        DateFormat format = formators.get(pattern);
        if (format == null) {
            format = new SimpleDateFormat(pattern);
            formators.put(pattern, format);
        }
        return format;
    }
}
