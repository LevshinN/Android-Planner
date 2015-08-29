package ru.levn.simpleplanner;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import ru.levn.simpleplanner.calendar.CalendarProvider;
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

    public static int sFragWidth;
    public static int sFragHeight;

    public static final String ENABLED_CALENDARS_DB = "enabledcaldb";

    public static SelectedDate sSelectedDate;
    public static EventsContainer sEvents;

    public static int sCurrentMode;
    public static View sBtnCurrentDate;

    public static boolean sIsDrawerClosed;

    private static Activity mMainActivity;

    public static void init(Activity activity) {
        sSelectedDate = new SelectedDate();
        sEvents = new EventsContainer(sSelectedDate.getDate().getTimeInMillis(), 6);
        mMainActivity = activity;
    }

    public static void sUpdateTitle() {
        ((TextView)sBtnCurrentDate.findViewById(R.id.txt_current_date))
                .setText(sGetCurrentDateAsText(sCurrentMode));
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

                String startWeek =  "" + day + " " + new DateFormatSymbols().getShortMonths()[month % 12];

                c.add(Calendar.WEEK_OF_YEAR, 1);

                day = c.get(Calendar.DAY_OF_MONTH);
                month = c.get(Calendar.MONTH);
                year = c.get(Calendar.YEAR);

                String endWeek = "" + day + " " + new DateFormatSymbols().getShortMonths()[month % 12];

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

    public static View.OnTouchListener onTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    ObjectAnimator o1 = ObjectAnimator.ofFloat(v, "cardElevation",
                            2 * Common.sScreenDensity ,
                            8 * Common.sScreenDensity ).setDuration(80);
                    o1.start();
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    ObjectAnimator o2 = ObjectAnimator.ofFloat(v, "cardElevation",
                            8 * Common.sScreenDensity ,
                            2 * Common.sScreenDensity ).setDuration(80);
                    o2.start();
                    break;
            }
            return false;
        }
    };

    public interface OnUpdateEventsInterface {
        public void onUpdate();
    }

    public static void onUpdate() {
        ((OnUpdateEventsInterface)mMainActivity).onUpdate();
    }
}

