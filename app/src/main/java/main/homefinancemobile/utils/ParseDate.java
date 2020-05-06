package main.homefinancemobile.utils;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class ParseDate {
    static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    public static String mToMm(int m) {
        String mm = String.valueOf(m);
        if (m < 10) {
            mm = "0" + mm;
        }
        return mm;
    }

    private static LocalDate getLocalDate(String dateValue) throws ParseException {
        Date date = sdf.parse(dateValue);
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static int getDayFromString(String dateValue) throws ParseException {
        return getLocalDate(dateValue).getDayOfMonth();
    }
    public static int getMonthFromString(String dateValue) throws ParseException {
        return getLocalDate(dateValue).getMonth().getValue() - 1;
    }
    public static int getYearFromString(String dateValue) throws ParseException {
        return getLocalDate(dateValue).getYear();
    }

    public static Date getDateFromString(String dateValue) throws ParseException {
        return sdf.parse(dateValue);
    }

    public static String parseDateToString(Date date) {
        return sdf.format(date);
    }
}
