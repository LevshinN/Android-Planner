package ru.levn.simpleplanner.calendar;

import android.app.Activity;
import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
import android.util.Pair;

import java.lang.reflect.Array;
import java.net.URI;
import java.security.KeyException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import ru.levn.simpleplanner.Common;

/**
 * Created by Levshin_N on 14.07.2015.
 */

public class CalendarProvider {
    public static ArrayList<Calendar> calendars;

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
            CalendarContract.Events.EVENT_COLOR,    // 2
            CalendarContract.Events.TITLE,          // 3
            CalendarContract.Events.DESCRIPTION,    // 4
            CalendarContract.Events.DTSTART,        // 5
            CalendarContract.Events.DTEND,          // 6
            CalendarContract.Events.DURATION,       // 7
            CalendarContract.Events.ALL_DAY,        // 8
            CalendarContract.Events.EVENT_LOCATION,  // 9
            CalendarContract.Events.ORIGINAL_ID     // 10
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
    private static final int PROJECTION_EVENT_LOCATION = 9;
    private static final int PROJECTION_EVENT_ORIGINAL_ID = 10;


    private static final String[] projectionInstance = new String[] {
            CalendarContract.Instances.EVENT_ID,    // 0
            CalendarContract.Instances.BEGIN,   // 1
            CalendarContract.Instances.END      // 2
    };

    private static final int PROJECTION_INSTANCE_EVENT_ID = 0;
    private static final int PROJECTION_INSTANCE_BEGIN = 1;
    private static final int PROJECTION_INSTANCE_END = 2;





    private static HashMap<String, Boolean> selectedCalendarsIDs;

    private static CalendarDBHelper dbHelper;
    private static SQLiteDatabase db;

    public static void initCalendarProvider (Activity activity) {
        calendarsUri = Uri.parse("content://com.android.calendar/calendars");
        calendars = new ArrayList<>();
        selectedCalendarsIDs = new HashMap<>();

        // подключаемся к БД
        dbHelper = new CalendarDBHelper(activity);
        db = dbHelper.getWritableDatabase();

        updateCalendars(activity);
    }

    private static void LoadDB() {
        Log.d("Databse", "Loading...");

        ContentValues cv = new ContentValues();
        Cursor c = db.query(Common.ENABLED_CALENDARS_DB, null, null, null, null, null, null);

        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex("id");
            int enabledColIndex = c.getColumnIndex("enabled");

            do {
                selectedCalendarsIDs.put( c.getString(idColIndex), c.getInt(enabledColIndex) != 0 );
            } while (c.moveToNext());
        } else
            Log.d("DATABASE", "database is empty");
        c.close();
    }

    private static void SaveDB() {
        Log.d("Databse", "Saving...");

        db.delete(Common.ENABLED_CALENDARS_DB, null, null);

        // создаем объект для данных
        ContentValues cv = new ContentValues();

        for ( Map.Entry<String, Boolean> row : selectedCalendarsIDs.entrySet()) {
            cv.put("id", row.getKey());
            cv.put("enabled", row.getValue() ? 1 : 0);
            db.insert(Common.ENABLED_CALENDARS_DB, null, cv);
        }
    }

    public static void updateCalendars(Activity activity) {
        calendars.clear();

        LoadDB();

        // Пробегаемся по всей базе календарей
        Cursor managedCursor = activity.getContentResolver().query(calendarsUri, projectionCalendar, null, null, null);
        if (managedCursor != null && managedCursor.moveToFirst())
        {
            String calendarID = null;
            String calendarAccName = null;
            String calendarDispName = null;
            String calendarOwnerAcc = null;

            do
            {
                calendarID = managedCursor.getString(PROJECTION_CALENDAR_ID_INDEX);
                calendarAccName = managedCursor.getString(PROJECTION_CALENDAR_ACCOUNT_NAME_INDEX);
                calendarDispName = managedCursor.getString(PROJECTION_CALENDAR_DISPLAY_NAME_INDEX);
                calendarOwnerAcc = managedCursor.getString(PROJECTION_CALENDAR_OWNER_ACCOUNT_INDEX);


                // Проверяем не новый ли календарь за счет того
                // что ищем его в всписке выбранных/отключенных календарей
                if (!selectedCalendarsIDs.containsKey(calendarID)) {
                    selectedCalendarsIDs.put(calendarID, true);
                }

                // Добавляем название в список
                Calendar cal = new Calendar();
                cal.id = calendarID;
                cal.account_name = calendarAccName;
                cal.display_name = calendarDispName;
                cal.owner_account = calendarOwnerAcc;
                cal.enabled = selectedCalendarsIDs.get(calendarID);

                calendars.add(cal);

            } while (managedCursor.moveToNext());
            managedCursor.close();
        }

        SaveDB();
    }

    public static void changeCalendarSelection(String id, boolean enabled) {
        if (!selectedCalendarsIDs.containsKey(id)) {
            System.err.println("ERROR: No such key in selectedCalendarsIDs: " + id);
        }
        selectedCalendarsIDs.put(id, enabled);
        SaveDB();
    }

    public static ArrayList<Calendar> getEnabledCalendarList () {
        ArrayList<Calendar> enabledCalendarList = new ArrayList<>();
        for (Calendar cal : calendars) {
            if (selectedCalendarsIDs.get(cal.id)) {
                enabledCalendarList.add(cal);
            }
        }
        return enabledCalendarList;
    }

    private static Event getEventById(Activity activity, String eventID) {
        String selection = "(" + CalendarContract.Events._ID    + " = ?)";
        String[] selectionArgs = new String[] { eventID };

        Cursor cEvent = activity.getContentResolver().query(CalendarContract.Events.CONTENT_URI, projectionEvent, selection, selectionArgs, null);

        if (cEvent != null && cEvent.moveToFirst() ) {
            Event event = new Event();
            event.CAL_ID = cEvent.getString(PROJECTION_EVENT_CALENDAR_ID_INDEX);
            event.EVENT_ID = cEvent.getString(PROJECTION_EVENT_ID_INDEX);
            event.COLOR = cEvent.getInt(PROJECTION_EVENT_COLOR);
            event.TITLE = cEvent.getString(PROJECTION_EVENT_TITLE);
            event.DESCRIPTION = cEvent.getString(PROJECTION_EVENT_DESCRIPTION);
            event.DT_START = cEvent.getLong(PROJECTION_EVENT_DTSTART);
            event.DT_END = cEvent.getLong(PROJECTION_EVENT_DTEND);
            event.DURATION = cEvent.getLong(PROJECTION_EVENT_DURATION);
            event.ALL_DAY = cEvent.getLong(PROJECTION_EVENT_ALL_DAY) > 0;
            event.EVENT_LOC = cEvent.getString(PROJECTION_EVENT_LOCATION);
            event.ORIGINAL_ID = cEvent.getString(PROJECTION_EVENT_ORIGINAL_ID);

            return event;
        }

        return null;
    }

    public static ArrayList<Event> getAvilableEventsForPeriod(Activity activity, long UTCStart, long UTCEnd) {
        ArrayList<Event> events = new ArrayList<>();

        Cursor c = CalendarContract.Instances.query(activity.getContentResolver(), projectionInstance, UTCStart, UTCEnd);

        if (c != null && c.moveToFirst()) {
            do {

                String eventID = c.getString(PROJECTION_INSTANCE_EVENT_ID);

                long eventStart = c.getLong(PROJECTION_INSTANCE_BEGIN);
                long eventEnd = c.getLong(PROJECTION_INSTANCE_END);

                // Отбрасываем события, которые пересекаются с текущим периодом одной миллисекундой.
                if (eventStart == UTCEnd || eventEnd == UTCStart) {
                    continue;
                }

                Event event = getEventById(activity, eventID);

                if (event != null) {
                    event.DT_START = eventStart;
                    event.DT_END = eventEnd;
                    events.add(event);
                }
            } while (c.moveToNext());
        }

        return events;
    }

    public static Pair<Long,Long> getDayPeriod() {

        java.util.Calendar cal = (java.util.Calendar)Common.GetSelectedDate().clone();



        long start = cal.getTimeInMillis();

        cal.add(java.util.Calendar.DAY_OF_MONTH, 1);

        long finish = cal.getTimeInMillis();

        return new Pair<>(start, finish - 1);
    }

    public static Pair<Long,Long> getWeekPeriod() {

        java.util.Calendar cal = (java.util.Calendar)Common.GetSelectedDate().clone();

        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);

        cal.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY);

        long start = cal.getTimeInMillis() + cal.get(java.util.Calendar.ZONE_OFFSET);


        cal.add(java.util.Calendar.WEEK_OF_YEAR, 1);
        long finish = cal.getTimeInMillis() + cal.get(java.util.Calendar.ZONE_OFFSET);

        return new Pair<>(start, finish);
    }

    public static String getTime(long UTCTime) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTimeInMillis(UTCTime);

        return String.format("%02d:%02d", cal.get(java.util.Calendar.HOUR_OF_DAY), cal.get(java.util.Calendar.MINUTE));
    }
}
