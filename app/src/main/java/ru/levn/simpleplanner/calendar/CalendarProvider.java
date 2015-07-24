package ru.levn.simpleplanner.calendar;

import android.app.Activity;
import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
    private static final String[] projection = new String[]{"_id", "name"};
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
        Cursor managedCursor = activity.getContentResolver().query(calendarsUri, projection, null, null, null);
        if (managedCursor != null && managedCursor.moveToFirst())
        {
            String calName;
            String calID;
            int nameColumn = managedCursor.getColumnIndex("name");
            int idColumn = managedCursor.getColumnIndex("_id");
            do
            {
                calName = managedCursor.getString(nameColumn);
                calID = managedCursor.getString(idColumn);
                if (calName != null) {
                    // Проверяем не новый ли календарь за счет того
                    // что ищем его в всписке выбранных/отключенных календарей
                    if (!selectedCalendarsIDs.containsKey(calID)) {
                        selectedCalendarsIDs.put(calID, true);
                    }

                    // Добавляем название в список
                    calendars.add(new Calendar(calName, calID, selectedCalendarsIDs.get(calID)));
                }
            } while (managedCursor.moveToNext());
            managedCursor.close();
        }

        SaveDB();
    }

    // Получить список доступных календарей
    public static ArrayList<String> GetCalendarsNames(Activity activity) {

        // Заводим список назаваний доступных календарей
        ArrayList<String> calendarsNames = new ArrayList<String>();

        for (Calendar cal: calendars) {
            calendarsNames.add(cal.getName());
        }

        return calendarsNames;
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
            if (selectedCalendarsIDs.get(cal.getId())) {
                enabledCalendarList.add(cal);
            }
        }
        return enabledCalendarList;
    }

}
