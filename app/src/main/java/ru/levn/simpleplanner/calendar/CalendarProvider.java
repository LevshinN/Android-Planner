package ru.levn.simpleplanner.calendar;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;

import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;

/**
 * Created by Levshin_N on 14.07.2015.
 */

public class CalendarProvider {
    private Uri calendarsUri;
    private static String[] projection = new String[]{"_id", "name"};

    public CalendarProvider () {
        calendarsUri = Uri.parse("content://com.android.calendar/calendars");
    }

    // ѕолучить список доступных календарей
    public ArrayList<String> GetCalendarsNames(Activity activity) {

        // «аводим список назаваний доступных календарей
        ArrayList<String> calendarsNames = new ArrayList<String>();

        // ѕробегаемс€ по всей базе календарей
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
                    // ƒобавл€ем название в список
                    calendarsNames.add(calName);
                }
            } while (managedCursor.moveToNext());
            managedCursor.close();
        }

        return calendarsNames;
    }
}
