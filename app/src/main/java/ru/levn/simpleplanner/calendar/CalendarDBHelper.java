package ru.levn.simpleplanner.calendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ru.levn.simpleplanner.Common;

/**
 * Created by Levshin_N on 24.07.2015.
 */
public class CalendarDBHelper extends SQLiteOpenHelper {

    public CalendarDBHelper(Context context) {
        // конструктор суперкласса
        super(context, "settings" , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DATABASE", "--- onCreate database ---");
        // создаем таблицу с полями
        db.execSQL("create table " + Common.ENABLED_CALENDARS_DB + " ("
                + "id integer primary key,"
                + "enabled integer"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
