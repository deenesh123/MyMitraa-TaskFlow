package com.mymitraa.taskflow.api_util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class StringUtil {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private StringUtil() {}

    public static Date convertSimpleStringToDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return new SimpleDateFormat(DEFAULT_DATE_FORMAT).parse(dateStr.trim());
        } catch (ParseException e) {
            try {
                return new SimpleDateFormat(DATE_TIME_FORMAT).parse(dateStr.trim());
            } catch (ParseException ex) {
                return new Date();
            }
        }
    }
}
