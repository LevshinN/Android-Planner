package ru.levn.simpleplanner;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import ru.levn.simpleplanner.calendar.CalendarProvider;

/**
 * Created by Levshin_N on 14.07.2015.
 */
public class Common {
    public static final int DAY_MODE = 0;
    public static final int WEEK_MODE = 1;
    public static final int MONTH_MODE = 2;

    public static String CALENDARS_LIST_KEY = "cal_names";
    public static String ENABLED_CALENDARS_DB = "enabledcaldb";

    public static final int DIALOG_DATE = 1001;
    private static Calendar selectedDate;

    public static CalendarProvider calendarProvider;

    public static int currentFragment;


    public static void initCurrentDate() {
        selectedDate = Calendar.getInstance();
    }


    public static Calendar GetSelectedDate() {
        return selectedDate;
    }

    public static void SetDate(int year, int month, int day) {
        selectedDate.set(year, month, day);

        // Нужно вызвать для того, чтобы принудить календарь пересчитать остальные значения.
        selectedDate.getTime();
    }
}
