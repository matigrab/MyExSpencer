package com.matpaw.myexspencer.utils;

import java.util.Calendar;
import java.util.Date;

public class Dates {
    public static Date get(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTime();
    }
}
