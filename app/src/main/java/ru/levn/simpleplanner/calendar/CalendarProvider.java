package ru.levn.simpleplanner.calendar;

import android.app.Activity;
import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import java.lang.reflect.Array;
import java.net.URI;
import java.security.KeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
            CalendarContract.Events.EVENT_LOCATION  // 7
    };

    // The indices for the projection array above.
    private static final int PROJECTION_EVENT_ID_INDEX = 0;
    private static final int PROJECTION_EVENT_CALENDAR_ID_INDEX = 1;
    private static final int PROJECTION_EVENT_COLOR = 2;
    private static final int PROJECTION_EVENT_TITLE = 3;
    private static final int PROJECTION_EVENT_DESCRIPTION = 4;
    private static final int PROJECTION_EVENT_DTSTART = 5;
    private static final int PROJECTION_EVENT_DTEND = 6;
    private static final int PROJECTION_EVENT_LOCATION = 7;



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

    public static ArrayList<Event> getAvailableEvents(Activity activity) {
        ArrayList<Event> events = new ArrayList<>();

        String selection = "(" + CalendarContract.Events.CALENDAR_ID + " = ?)";
        String[] selectionArgs;

        for ( Calendar cal : calendars ) {
            if ( selectedCalendarsIDs.get(cal.id) ) {
                Cursor c = null;
                selectionArgs = new String[]{cal.id};

                c = activity.getContentResolver().query(CalendarContract.Events.CONTENT_URI, projectionEvent, selection, selectionArgs, null);

                if (c != null && c.moveToFirst()) {
                    do {
                        Event event = new Event();

                        String title = c.getString(PROJECTION_EVENT_TITLE);

                        if (title == null) {
                            continue;
                        }

                        event.CAL_ID = c.getString(PROJECTION_EVENT_CALENDAR_ID_INDEX);
                        event.EVENT_ID = c.getString(PROJECTION_EVENT_ID_INDEX);
                        event.COLOR = c.getString(PROJECTION_EVENT_COLOR);
                        event.TITLE = c.getString(PROJECTION_EVENT_TITLE);
                        event.DESCRIPTION = c.getString(PROJECTION_EVENT_DESCRIPTION);
                        event.DT_START = c.getLong(PROJECTION_EVENT_DTSTART);
                        event.DT_END = c.getLong(PROJECTION_EVENT_DTEND);
                        event.EVENT_LOC = c.getString(PROJECTION_EVENT_LOCATION);

                        events.add(event);

                    } while (c.moveToNext());
                }
            }
        }

        return events;
    }



}
