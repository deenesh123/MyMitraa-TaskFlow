package com.mymitraa.taskflow.api_util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

public class StringUtil {

    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";

    public static boolean haveValue(String val) {
        return val != null && !val.trim().isEmpty() && !"null".equalsIgnoreCase(val.trim());
    }

    public static boolean haveElements(Collection<?> col) {
        return col != null && !col.isEmpty();
    }

    public static boolean isNumeric(String str) {
        if (!haveValue(str)) {
            return false;
        }
        try {
            Double.parseDouble(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Date convertStringIntoDatePattern(String dateStr) {
        if (!haveValue(dateStr)) {
            return null;
        }
        String[] patterns = new String[] {
            "yyyy-MM-dd", "dd-MM-yyyy", "MM/dd/yyyy", "yyyy/MM/dd",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss"
        };
        for (String pattern : patterns) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                sdf.setLenient(false);
                return sdf.parse(dateStr.trim());
            } catch (ParseException ignored) {
            }
        }
        return null;
    }

    public static Date convertSimpleStringToDate(String dateStr) {
        return convertStringIntoDatePattern(dateStr);
    }

    public static String convertDateIntoStringPattern(Date date) {
        if (date == null) return null;
        return new SimpleDateFormat(DEFAULT_DATE_PATTERN).format(date);
    }

    public static String convertDateIntoYYMMdd(Date date) {
        if (date == null) return null;
        return new SimpleDateFormat("yy-MM-dd").format(date);
    }

    public static Date getDayStartTimeFromSimpleDateString(String dateStr) {
        Date d = convertStringIntoDatePattern(dateStr);
        if (d == null) return null;
        return setStringToStartDay(d);
    }

    public static Date getDayStartTimeFromSimpleDateString1(String dateStr) {
        return getDayStartTimeFromSimpleDateString(dateStr);
    }

    public static Date getDayEndTimeFromSimpleDateString(String dateStr) {
        Date d = convertStringIntoDatePattern(dateStr);
        if (d == null) return null;
        return setStringToEndDay(d);
    }

    public static Date getDayEndTimeFromSimpleDateString1(String dateStr) {
        return getDayEndTimeFromSimpleDateString(dateStr);
    }

    public static Date setStringToStartDay(Date date) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date setDateBegniningOfTheDay(Date date) {
        return setStringToStartDay(date);
    }

    public static Date setDateBegniningOfTheDayNew(Date date) {
        return setStringToStartDay(date);
    }

    public static Date setStringToEndDay(Date date) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    public static Date setDateEndOfTheDay(Date date) {
        return setStringToEndDay(date);
    }

    public static Date setDateEndOfTheDayNew(Date date) {
        return setStringToEndDay(date);
    }

    public static Date addDays(Date date, int days) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return cal.getTime();
    }

    public static Date subDays(Date date, int days) {
        return addDays(date, -days);
    }
}
