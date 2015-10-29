package net.frebib.sscdatabase.util;

import java.sql.Date;

public class DateHelper {
    public final static long DAY    = 86400000L;
    public final static long MONTH  = 2592000000L;
    public final static long YEAR   = 31556952000L;

    public static long dateOf(int year, int month, int day) {
        return YEAR * (year - 1970) + MONTH * (month - 1) + DAY * day;
    }
    public static long timespan(int years, int months, int days) {
        return YEAR * years + MONTH * months + DAY * days;
    }
}
