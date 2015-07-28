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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import ru.levn.simpleplanner.Common;
import ru.levn.simpleplanner.R;
import ru.levn.simpleplanner.adapter.EventListAdapter;
import ru.levn.simpleplanner.calendar.CalendarProvider;
import ru.levn.simpleplanner.calendar.Event;

/**
 * Created by Levshin_N on 14.07.2015.
 */
public class ScreenDay extends Fragment {

    private Context context;
    private View rootView;
    private ListView eventList;

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


        rootView = inflater.inflate(R.layout.day, container, false);
        context = getActivity().getApplicationContext();
        eventList = (ListView)rootView.findViewById(R.id.day_event_list);

        update();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Calendar c = new GregorianCalendar(TimeZone.getTimeZone("UTC"));

        c.clear();
        c.set(Common.year, Common.month, Common.day, 0, 0, 0);
        long start = c.getTimeInMillis();

        c.clear();
        c.set(Common.year, Common.month, Common.day, 23, 59, 59);
        long finish = c.getTimeInMillis();

        ArrayList<Event> events = CalendarProvider.getAvilableEventsForPeriod(this.getActivity(), start, finish);
        final EventListAdapter mListAdapter = new EventListAdapter(this.getActivity(), events);
        eventList.setAdapter(mListAdapter);

        Toast.makeText(this.getActivity(), "" + events.size(), Toast.LENGTH_SHORT).show();
    }

    private void update() {
        TextView date = (TextView)rootView.findViewById(R.id.day_text_view);
        date.setText( Common.getTextCurrentDate() );
    }
}
