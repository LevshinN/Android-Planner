package ru.levn.simpleplanner.fragment;



import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    private EventListAdapter eventListAdapter;

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

        Pair<Long,Long> period = CalendarProvider.getDayPeriod();

        ArrayList<Event> events = Common.calendarProvider.getAvilableEventsForPeriod(this.getActivity(), period.first, period.second);
        eventListAdapter = new EventListAdapter(this.getActivity(), events);
        eventList.setAdapter(eventListAdapter);
        eventList.setOnItemClickListener(selectItemListener);

        Toast.makeText(this.getActivity(), "" + events.size(), Toast.LENGTH_SHORT).show();
    }

    private void update() {
        TextView date = (TextView)rootView.findViewById(R.id.day_text_view);
        date.setText(CalendarProvider.getTextCurrentDate(Common.currentFragment, Common.GetSelectedDate().getTimeInMillis()));
    }

    AdapterView.OnItemClickListener selectItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Event event = (Event)parent.getItemAtPosition(position);
            DialogFragment event_info = EventInfo.newInstance(0, event);
            event_info.show(getFragmentManager(), "event_info");
        }
    };


}
