package ru.levn.simpleplanner.calendar;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
import android.util.Pair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import ru.levn.simpleplanner.Common;
import ru.levn.simpleplanner.R;
import ru.levn.simpleplanner.calendar.common.accounts.GenericAccountService;
import ru.levn.simpleplanner.calendar.syncadapter.SyncUtils;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 14.07.2015.
 */

public class CalendarProvider {
    public static ArrayList<MyCalendar> calendars;
    private static Uri calendarsUri;

    private static final String[] projectionCalendar = new String[]{
            CalendarContract.Calendars._ID,                     // 0
            CalendarContract.Calendars.ACCOUNT_NAME,            // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,   // 2
            CalendarContract.Calendars.OWNER_ACCOUNT            // 3
    };

    // The indices for the projection array above.
    private static final int PROJECTION_CALENDAR_ID_INDEX = 0;
    private static final int PROJECTION_CALENDAR_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_CALENDAR_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_CALENDAR_OWNER_ACCOUNT_INDEX = 3;

    private static final String[] projectionEvent = new String[] {
            CalendarContract.Events._ID,            // 0
            CalendarContract.Events.CALENDAR_ID,    // 1
            CalendarContract.Events.DISPLAY_COLOR,  // 2
            CalendarContract.Events.TITLE,          // 3
            CalendarContract.Events.DESCRIPTION,    // 4
            CalendarContract.Events.DTSTART,        // 5
            CalendarContract.Events.DTEND,          // 6
            CalendarContract.Events.DURATION,       // 7
            CalendarContract.Events.ALL_DAY,        // 8
            CalendarContract.Events.EVENT_TIMEZONE, // 9
            CalendarContract.Events.EVENT_LOCATION, // 10
            CalendarContract.Events.ORIGINAL_ID,    // 11
            CalendarContract.Events.RRULE,          // 12
            CalendarContract.Events.RDATE,          // 13
            CalendarContract.Events.EXRULE,         // 14
            CalendarContract.Events.EXDATE,         // 15
            CalendarContract.Events.ORIGINAL_INSTANCE_TIME  // 16
    };

    // The indices for the projection array above.
    private static final int PROJECTION_EVENT_ID_INDEX = 0;
    private static final int PROJECTION_EVENT_CALENDAR_ID_INDEX = 1;
    private static final int PROJECTION_EVENT_COLOR = 2;
    private static final int PROJECTION_EVENT_TITLE = 3;
    private static final int PROJECTION_EVENT_DESCRIPTION = 4;
    private static final int PROJECTION_EVENT_DTSTART = 5;
    private static final int PROJECTION_EVENT_DTEND = 6;
    private static final int PROJECTION_EVENT_DURATION = 7;
    private static final int PROJECTION_EVENT_ALL_DAY = 8;
    private static final int PROJECTION_EVENT_TIME_ZONE = 9;
    private static final int PROJECTION_EVENT_LOCATION = 10;
    private static final int PROJECTION_EVENT_ORIGINAL_ID = 11;
    private static final int PROJECTION_EVENT_RRULE = 12;
    private static final int PROJECTION_EVENT_RDATE = 13;
    private static final int PROJECTION_EVENT_EXRULE = 14;
    private static final int PROJECTION_EVENT_EXDATE = 15;
    private static final int PROJECTION_EVENT_INSTANCE_TIME = 16;

    private static final String[] projectionInstance = new String[] {
            CalendarContract.Instances.EVENT_ID,    // 0
            CalendarContract.Instances.BEGIN,       // 1
            CalendarContract.Instances.END          // 2
    };

    private static final int PROJECTION_INSTANCE_EVENT_ID = 0;
    private static final int PROJECTION_INSTANCE_BEGIN = 1;
    private static final int PROJECTION_INSTANCE_END = 2;


    private static HashMap<String, Boolean> mSelectedCalendarsIDs;
    private static int[] mColors;

    private static SQLiteDatabase mDataBase;

    private static ContentResolver mContentResolver;

    public static void sInitCalendarProvider (Activity activity) {
        calendarsUri = Uri.parse("content://com.android.calendar/calendars");
        calendars = new ArrayList<>();
        mSelectedCalendarsIDs = new HashMap<>();

        // подключаемся к БД
        CalendarDBHelper dbHelper = new CalendarDBHelper(activity);
        mDataBase = dbHelper.getWritableDatabase();
        mContentResolver = activity.getContentResolver();
        mColors = activity.getResources().getIntArray(R.array.event_colors_values);

        sUpdateCalendars();
    }

    private static void sLoadDB() {
        Log.d("Databse", "Loading...");

        Cursor c = mDataBase.query(Common.ENABLED_CALENDARS_DB, null, null, null, null, null, null);

        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex("id");
            int enabledColIndex = c.getColumnIndex("enabled");

            do {
                mSelectedCalendarsIDs.put( c.getString(idColIndex), c.getInt(enabledColIndex) != 0 );
            } while (c.moveToNext());
        } else
            Log.d("DATABASE", "database is empty");
        c.close();
    }

    private static void sSaveDB() {
        Log.d("Databse", "Saving...");

        mDataBase.delete(Common.ENABLED_CALENDARS_DB, null, null);

        // создаем объект для данных
        ContentValues cv = new ContentValues();

        for ( Map.Entry<String, Boolean> row : mSelectedCalendarsIDs.entrySet()) {
            cv.put("id", row.getKey());
            cv.put("enabled", row.getValue() ? 1 : 0);
            mDataBase.insert(Common.ENABLED_CALENDARS_DB, null, cv);
        }
    }

    public static void sUpdateCalendars() {
        calendars.clear();

        sLoadDB();

        // Пробегаемся по всей базе календарей
        Cursor managedCursor = mContentResolver.query(calendarsUri, projectionCalendar, null, null, null);
        if (managedCursor != null && managedCursor.moveToFirst())
        {
            String calendarID;
            String calendarAccName;
            String calendarDispName;
            String calendarOwnerAcc;

            do
            {
                calendarID = managedCursor.getString(PROJECTION_CALENDAR_ID_INDEX);
                calendarAccName = managedCursor.getString(PROJECTION_CALENDAR_ACCOUNT_NAME_INDEX);
                calendarDispName = managedCursor.getString(PROJECTION_CALENDAR_DISPLAY_NAME_INDEX);
                calendarOwnerAcc = managedCursor.getString(PROJECTION_CALENDAR_OWNER_ACCOUNT_INDEX);


                // Проверяем не новый ли календарь за счет того
                // что ищем его в всписке выбранных/отключенных календарей
                if (!mSelectedCalendarsIDs.containsKey(calendarID)) {
                    mSelectedCalendarsIDs.put(calendarID, true);
                }

                // Добавляем название в список
                MyCalendar cal = new MyCalendar();
                cal.id = calendarID;
                cal.accountName = calendarAccName;
                cal.displayName = calendarDispName;
                cal.ownerAccount = calendarOwnerAcc;
                cal.enabled = mSelectedCalendarsIDs.get(calendarID);

                calendars.add(cal);

            } while (managedCursor.moveToNext());
            managedCursor.close();
        }

        sSaveDB();
    }

    public static void changeCalendarSelection(String id, boolean enabled) {
        if (!mSelectedCalendarsIDs.containsKey(id)) {
            System.err.println("ERROR: No such key in mSelectedCalendarsIDs: " + id);
        }
        mSelectedCalendarsIDs.put(id, enabled);
        sSaveDB();
    }

    private static Event getEventById(String eventID) {
        String selection = "(" + CalendarContract.Events._ID    + " = ?)";
        String[] selectionArgs = new String[] { eventID };

        Cursor cEvent = mContentResolver.query(CalendarContract.Events.CONTENT_URI, projectionEvent, selection, selectionArgs, null);

        if (cEvent != null && cEvent.moveToFirst() ) {
            Event event = new Event();
            event.calendarId = cEvent.getString(PROJECTION_EVENT_CALENDAR_ID_INDEX);
            event.id = cEvent.getString(PROJECTION_EVENT_ID_INDEX);
            event.color = cEvent.getInt(PROJECTION_EVENT_COLOR);
            event.title = cEvent.getString(PROJECTION_EVENT_TITLE);
            event.description = cEvent.getString(PROJECTION_EVENT_DESCRIPTION);
            event.timeOriginalStart = cEvent.getLong(PROJECTION_EVENT_DTSTART);
            event.timeOriginalEnd = cEvent.getLong(PROJECTION_EVENT_DTEND);
            event.duration = cEvent.getLong(PROJECTION_EVENT_DURATION);
            event.isAllDay = cEvent.getLong(PROJECTION_EVENT_ALL_DAY) > 0;
            event.timeZone = cEvent.getString(PROJECTION_EVENT_TIME_ZONE);
            event.location = cEvent.getString(PROJECTION_EVENT_LOCATION);
            event.originalId = cEvent.getString(PROJECTION_EVENT_ORIGINAL_ID);
            event.rrule = cEvent.getString(PROJECTION_EVENT_RRULE);
            event.exrule = cEvent.getString(PROJECTION_EVENT_EXRULE);
            event.rdate = cEvent.getString(PROJECTION_EVENT_RDATE);
            event.exdate = cEvent.getString(PROJECTION_EVENT_EXDATE);
            event.originalInstanceTime = cEvent.getLong(PROJECTION_EVENT_INSTANCE_TIME);

            cEvent.close();

            return event;
        }

        return null;
    }

    protected static ArrayList<Event> getAvilableEventsForPeriod(long UTCStart, long UTCEnd) {
        ArrayList<Event> events = new ArrayList<>();

        Cursor c = CalendarContract.Instances.query(mContentResolver, projectionInstance, UTCStart, UTCEnd);

        if (c != null && c.moveToFirst()) {
            do {

                String eventID = c.getString(PROJECTION_INSTANCE_EVENT_ID);

                long eventStart = c.getLong(PROJECTION_INSTANCE_BEGIN);
                long eventEnd = c.getLong(PROJECTION_INSTANCE_END);

                // Отбрасываем события, которые пересекаются с текущим периодом одной миллисекундой.
                if (eventStart == UTCEnd || eventEnd == UTCStart) {
                    continue;
                }

                Event event = getEventById(eventID);

                if (event != null && mSelectedCalendarsIDs.get(event.calendarId)) {
                    event.timeStart = eventStart;
                    event.timeEnd = eventEnd;
                    events.add(event);
                }
            } while (c.moveToNext());
        }

        if (c != null) c.close();

        return events;
    }

    public static Pair<Long,Long> getDayPeriod(long UtcTime, boolean UtcTimezone) {

        Calendar cal = Calendar.getInstance();

        cal.setTimeInMillis(UtcTime);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if (UtcTimezone) cal.setTimeZone(TimeZone.getTimeZone("UTC"));

        long start = cal.getTimeInMillis();

        cal.add(Calendar.DAY_OF_MONTH, 1);

        long finish = cal.getTimeInMillis();

        return new Pair<>(start, finish);
    }

    public static Pair<Long,Long> getWeekPeriod(long UtcTime) {

        Calendar cal = Calendar.getInstance();

        cal.setTimeInMillis(UtcTime);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        long start = cal.getTimeInMillis();


        cal.add(Calendar.WEEK_OF_YEAR, 1);
        long finish = cal.getTimeInMillis();

        return new Pair<>(start, finish);
    }

    public static long getNextPeriod(boolean forward) {

        int diff = forward ? 1 : -1;

        Calendar cal = (Calendar)Common.sSelectedDate.getDate().clone();

        switch (Common.sCurrentMode) {
            case Common.DAY_MODE:
                cal.add(Calendar.DAY_OF_MONTH, diff);
                return cal.getTimeInMillis();
            case Common.WEEK_MODE:
                cal.add(Calendar.WEEK_OF_YEAR, diff);
                return cal.getTimeInMillis();
        }

        return 0;
    }

    public static void moveSelectedDate(boolean forward) {
        int diff = forward ? 1 : -1;

        Calendar cal = Common.sSelectedDate.getDate();

        switch (Common.sCurrentMode) {
            case Common.DAY_MODE:
                cal.add(Calendar.DAY_OF_MONTH, diff);
                break;
            case Common.WEEK_MODE:
                cal.add(Calendar.WEEK_OF_YEAR, diff);
                break;
        }
    }

    public static String getTime(long UTCTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTimeInMillis(UTCTime);

        return String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY) , cal.get(Calendar.MINUTE));
    }

    public static String getDate(long UtcTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTimeInMillis(UtcTime);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        return dateFormat.format(cal.getTimeInMillis());
    }

    private static Uri asSyncAdapter(Uri uri, String account, String accountType) {
        return uri
                .buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, account)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, accountType)
                .build();
    }

    public static void saveNewEvent( Event event ) {
        ContentResolver cr = mContentResolver;
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, event.timeStart);
        values.put(CalendarContract.Events.DTEND, event.timeEnd);
        values.put(CalendarContract.Events.TITLE, event.title);
        values.put(CalendarContract.Events.DESCRIPTION, event.description);
        values.put(CalendarContract.Events.EVENT_LOCATION, event.location);
        values.put(CalendarContract.Events.EVENT_COLOR, event.color);
        values.put(CalendarContract.Events.CALENDAR_ID, event.calendarId);
        values.put(CalendarContract.Events.ALL_DAY, event.isAllDay);

        if (event.isAllDay) {
            Calendar c = new GregorianCalendar();
            c.setTimeInMillis(event.timeStart);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            values.put(CalendarContract.Events.DTSTART, c.getTimeInMillis());

            c.setTimeInMillis(event.timeEnd);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            values.put(CalendarContract.Events.DTEND, c.getTimeInMillis());

            values.put(CalendarContract.Events.EVENT_TIMEZONE, "UTC");
        } else {
            values.put(CalendarContract.Events.DTSTART, event.timeStart);
            values.put(CalendarContract.Events.DTEND, event.timeEnd);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getDisplayName()); // TODO Добавить таймзоны
        }

        if (event.originalId != null) {
            values.put(CalendarContract.Events.ORIGINAL_ID, event.originalId);
            values.put(CalendarContract.Events.ORIGINAL_INSTANCE_TIME, event.originalInstanceTime);
        }

        cr.insert(CalendarContract.Events.CONTENT_URI, values);
    }

    public static void editEvent( Event originalEvent, Event newEvent ) {
        // Если событие - часть рекурентной цепочки
        if ( originalEvent.rrule != null ) {
            newEvent.originalId = originalEvent.id;
            newEvent.originalInstanceTime = originalEvent.timeStart;
            saveNewEvent(newEvent);

        } else { // Если событие одиночное и ни к каким другим не привязано

            ContentResolver cr = mContentResolver;
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.TITLE, newEvent.title);
            values.put(CalendarContract.Events.DESCRIPTION, newEvent.description);
            values.put(CalendarContract.Events.EVENT_LOCATION, newEvent.location);
            values.put(CalendarContract.Events.EVENT_COLOR, newEvent.color);
            values.put(CalendarContract.Events.CALENDAR_ID, newEvent.calendarId);
            values.put(CalendarContract.Events.ALL_DAY, newEvent.isAllDay);

            if (newEvent.isAllDay) {
                Calendar c = new GregorianCalendar();
                c.setTimeInMillis(newEvent.timeStart);
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                values.put(CalendarContract.Events.DTSTART, c.getTimeInMillis());

                c.setTimeInMillis(newEvent.timeEnd);
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                values.put(CalendarContract.Events.DTEND, c.getTimeInMillis());

                values.put(CalendarContract.Events.EVENT_TIMEZONE, "UTC");
            } else {
                values.put(CalendarContract.Events.DTSTART, newEvent.timeStart);
                values.put(CalendarContract.Events.DTEND, newEvent.timeEnd);
                values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getDisplayName()); // TODO Добавить таймзоны
            }

            Uri syncUri = CalendarProvider.asSyncAdapter(CalendarContract.Events.CONTENT_URI, GenericAccountService.ACCOUNT_NAME, SyncUtils.ACCOUNT_TYPE);
            Uri updateUri = ContentUris.withAppendedId(syncUri, Integer.valueOf(newEvent.id));

            cr.update(updateUri, values, null, null);

        }
    }
}
