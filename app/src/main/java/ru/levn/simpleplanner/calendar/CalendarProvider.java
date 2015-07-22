package ru.levn.simpleplanner.calendar;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;

import java.lang.reflect.Array;
import java.net.URI;
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
    private static Uri calendarsUri;
    private static final String[] projection = new String[]{"_id", "name"};
    private static Map<String, String> calendars;

    public CalendarProvider (Activity activity) {
        calendarsUri = Uri.parse("content://com.android.calendar/calendars");
        calendars = new HashMap<String, String>();
        updateCalendars(activity);
    }

    public static void updateCalendars(Activity activity) {
        calendars.clear();
        HashMap<String, Boolean> newSelectedCalendarIDs = new HashMap<String, Boolean>();

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
                    // Добавляем название в список
                    calendars.put(calID, calName);
                    newSelectedCalendarIDs.put(calID, true);
                }
            } while (managedCursor.moveToNext());
            managedCursor.close();
        }

        for (Map.Entry<String, Boolean> val : newSelectedCalendarIDs.entrySet()) {
            if (Common.selectedCalendarsIDs.containsKey(val.getKey()) && !Common.selectedCalendarsIDs.get(val.getKey())) {
                newSelectedCalendarIDs.put(val.getKey(), val.getValue());
            }
        }

        Common.selectedCalendarsIDs = newSelectedCalendarIDs;
    }

    // Получить список доступных календарей
    public static ArrayList<String> GetCalendarsNames(Activity activity) {

        // Заводим список назаваний доступных календарей
        ArrayList<String> calendarsNames = new ArrayList<String>();

        for (String name: calendars.values()) {
            calendarsNames.add(name);
        }

        return calendarsNames;
    }

    public static Map<String,String> GetCalendars () {
        return calendars;
    }

}
