package ru.levn.simpleplanner.fragment;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import ru.levn.simpleplanner.Common;
import ru.levn.simpleplanner.R;

/**
 * Created by Levshin_N on 14.07.2015.
 */
public class ScreenDay extends Fragment {

    private Context context;

    // Projection array. Creating indices for this array instead of doing
    // dynamic lookups improves performance.
    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    public ScreenDay() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.day, container,
                false);

        context = getActivity().getApplicationContext();

        TextView date = (TextView)rootView.findViewById(R.id.day_text_view);

        date.setText( Common.getTextCurrentDate() );

        return rootView;
    }

    private void getTasks(int date) {
        Cursor cur = null;
        ContentResolver cr = context.getContentResolver();

        Uri uri = CalendarContract.Calendars.CONTENT_URI;


        String[] selectionArgs = new String[] {"levshin.niklay@phystech.edu", "com.google",
                "sampleuser@gmail.com"};

    }
}
