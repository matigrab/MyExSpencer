package com.matpaw.myexspencer.utils;

import java.util.Calendar;
import java.util.Date;

public class Dates {
    public static Date get(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTime();
    }

    public static String format(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int month = calendar.get(Calendar.MONTH);
        String monthString = (month < 10) ? "0" + month : "" + month;

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String dayString = (day < 10) ? "0" + day : "" + day;

        return calendar.get(Calendar.YEAR) + "-" + monthString + "-" + dayString;
    }
}
