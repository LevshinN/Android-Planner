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
    public static Calendar selectedDate;

    public static CalendarProvider calendarProvider;

    public static int currentFragment;


    public static void initCurrentDate() {
        selectedDate = Calendar.getInstance();
    }

    public static String getTextCurrentDate(int mode) {
        SimpleDateFormat dateFormat = null;

        switch (mode) {
            case DAY_MODE:
                dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                return dateFormat.format(selectedDate.getTime());

            case WEEK_MODE:

                Calendar c = (Calendar)selectedDate.clone();
                c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());

                int day = c.get(Calendar.DAY_OF_MONTH);
                int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);

                String startWeek =  "" + day + " " + new DateFormatSymbols().getShortMonths()[month % 12] + " " + year;

                c.add(Calendar.WEEK_OF_YEAR, 1);

                day = c.get(Calendar.DAY_OF_MONTH);
                month = c.get(Calendar.MONTH);
                year = c.get(Calendar.YEAR);

                String endWeek = "" + day + " " + new DateFormatSymbols().getShortMonths()[month % 12] + " " + year;

                return startWeek + " - " + endWeek;

            case MONTH_MODE:

                dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                return dateFormat.format(selectedDate.getTime());
        }

        return null;
    }
}
