package ru.levn.simpleplanner;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Levshin_N on 14.07.2015.
 */
public class Common {
    public static String CALENDARS_LIST_KEY = "cal_names";
    public static String ENABLED_CALENDARS_DB = "enabledcaldb";

    public static final int DIALOG_DATE = 1001;
    public static int day;
    public static int month;
    public static int year;

    public static int currentFragment;


    public static void initCurrentDate() {
        Calendar c = Calendar.getInstance();
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);
    }

    public static String getTextCurrentDate() {
        return "" + day + " " + new DateFormatSymbols().getMonths()[month % 12] + " " + year;
    }
}
