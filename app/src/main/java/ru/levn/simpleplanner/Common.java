package ru.levn.simpleplanner;

import android.widget.Button;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import ru.levn.simpleplanner.calendar.CalendarProvider;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 14.07.2015.
 */

public class Common {
    public static final int DAY_MODE = 0;
    public static final int WEEK_MODE = 1;
    public static final int MONTH_MODE = 2;

    public static final String ENABLED_CALENDARS_DB = "enabledcaldb";

    public static SelectedDate sSelectedDate;
    public static CalendarProvider sCalendarProvider;

    public static int sCurrentMode;
    public static Button sBtnCurrentDate;

    public static void init() {
        sSelectedDate = new SelectedDate();
    }


    public static void sUpdateTitle() {
        sBtnCurrentDate.setText(sGetCurrentDateAsText(sCurrentMode));
    }

    public static String sGetCurrentDateAsText(int mode) {
        SimpleDateFormat dateFormat;
        long UTCDate = sSelectedDate.getDate().getTimeInMillis();

        switch (mode) {
            case DAY_MODE:
                dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                return dateFormat.format(UTCDate);

            case WEEK_MODE:

                java.util.Calendar c = new GregorianCalendar();

                c.setTimeInMillis(UTCDate);

                c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

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
                return dateFormat.format(UTCDate);
        }

        return null;
    }

    public static class SelectedDate {
        private Calendar mDate;

        public  SelectedDate() {
            mDate = Calendar.getInstance();
        }

        public void setDate(int year, int month, int day) {
            mDate.set(year, month, day);

            // Нужно вызвать для того, чтобы принудить календарь пересчитать остальные значения.
            mDate.getTime();
        }

        public void  setDate( Calendar sourceDate ) {
            mDate = sourceDate;
        }

        public Calendar getDate() { return mDate; }
    }
}

