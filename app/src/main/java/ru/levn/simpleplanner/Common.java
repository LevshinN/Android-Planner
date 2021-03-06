package ru.levn.simpleplanner;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import ru.levn.simpleplanner.calendar.EventsContainer;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 14.07.2015.
 */

public class Common {
    public static final int DAY_MODE = 0;
    public static final int WEEK_MODE = 1;
    public static final int MONTH_MODE = 2;

    public static int sScreenWidth;
    public static int sScreenHeight;
    public static float sScreenDensity;

    public static final String ENABLED_CALENDARS_DB = "enabledcaldb";

    public static SelectedDate sSelectedDate;
    public static EventsContainer sEvents;

    public static int sCurrentMode;
    public static View sBtnCurrentDate;

    public static boolean sIsDrawerClosed;

    public static Activity sMainActivity;

    public static void init() {
        sSelectedDate = new SelectedDate();
        sEvents = new EventsContainer(sSelectedDate.getDate().getTimeInMillis(), 50);
    }

    public static void sUpdateTitle(Calendar date) {
        ((TextView)sBtnCurrentDate.findViewById(R.id.txt_current_date))
                .setText(sGetCurrentDateAsText(date, sCurrentMode));
    }

    public static String sGetCurrentDateAsText(Calendar date, int mode) {
        SimpleDateFormat dateFormat;
        long UTCDate = date.getTimeInMillis();

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

                String startWeek =  "" + day + " " + new DateFormatSymbols().getShortMonths()[month % 12];

                c.add(Calendar.WEEK_OF_YEAR, 1);

                day = c.get(Calendar.DAY_OF_MONTH);
                month = c.get(Calendar.MONTH);

                String endWeek = "" + day + " " + new DateFormatSymbols().getShortMonths()[month % 12];

                return startWeek + " - " + endWeek;

            case MONTH_MODE:
                String monthName = getMonthName(date.get(Calendar.MONTH));
                String firstLetter = monthName.substring(0,1).toUpperCase();
                monthName = firstLetter + monthName.substring(1);
                return monthName + ' ' + String.valueOf(date.get(Calendar.YEAR));
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

    public interface OnUpdateEventsInterface {
        void onUpdate();
    }

    public static void onUpdate() {
        //sEvents.update();
        ((OnUpdateEventsInterface)sMainActivity).onUpdate();
    }

    public static String getMonthName(int month) {
        String[] months;
        try {
            months = sMainActivity.getResources().getStringArray(R.array.month_names);
        } catch (Resources.NotFoundException ex) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
            Calendar calendar = new GregorianCalendar(2000, month, 1);
            return dateFormat.format(calendar.getTimeInMillis());
        }
        return months[month];
    }

    public static Resources sGetResources() {
        return sMainActivity.getResources();
    }
}

