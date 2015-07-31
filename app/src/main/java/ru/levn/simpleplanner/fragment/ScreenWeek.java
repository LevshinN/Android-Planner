package ru.levn.simpleplanner.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import ru.levn.simpleplanner.Common;
import ru.levn.simpleplanner.R;
import ru.levn.simpleplanner.adapter.EventListAdapter;
import ru.levn.simpleplanner.adapter.WeekListAdapter;
import ru.levn.simpleplanner.calendar.CalendarProvider;
import ru.levn.simpleplanner.calendar.Event;

/**
 * Created by Levshin_N on 14.07.2015.
 */
public class ScreenWeek extends Fragment {

    private Context context;
    private View rootView;
    private EventListAdapter eventListAdapter;

    public ScreenWeek() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.week, container, false);
        context = getActivity().getApplicationContext();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Pair<Long,Long> period = CalendarProvider.getWeekPeriod();

        ((TextView)rootView.findViewById(R.id.week_table_start_period)).setText("" + period.first);
        ((TextView)rootView.findViewById(R.id.week_table_end_period)).setText("" + period.second);

        long start = period.first;
        long end = period.second;
        long dayDuration = (end - start) / 7;

        // Понедельник
        ArrayList<Event> events = Common.calendarProvider.getAvilableEventsForPeriod(this.getActivity(), start, start + dayDuration);
        WeekListAdapter adapter = new WeekListAdapter(this.getActivity(), events);
        ListView lv = (ListView)rootView.findViewById(R.id.week_table_day_1);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(selectItemListener);

        // Вторник
        start += dayDuration;
        events = Common.calendarProvider.getAvilableEventsForPeriod(this.getActivity(), start, start + dayDuration);
        adapter = new WeekListAdapter(this.getActivity(), events);
        lv = (ListView)rootView.findViewById(R.id.week_table_day_2);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(selectItemListener);

        // Среда
        start += dayDuration;
        events = Common.calendarProvider.getAvilableEventsForPeriod(this.getActivity(), start, start + dayDuration);
        adapter = new WeekListAdapter(this.getActivity(), events);
        lv = (ListView)rootView.findViewById(R.id.week_table_day_3);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(selectItemListener);

        // Четверг
        start += dayDuration;
        events = Common.calendarProvider.getAvilableEventsForPeriod(this.getActivity(), start, start + dayDuration);
        adapter = new WeekListAdapter(this.getActivity(), events);
        lv = (ListView)rootView.findViewById(R.id.week_table_day_4);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(selectItemListener);

        // Пятница
        start += dayDuration;
        events = Common.calendarProvider.getAvilableEventsForPeriod(this.getActivity(), start, start + dayDuration);
        adapter = new WeekListAdapter(this.getActivity(), events);
        lv = (ListView)rootView.findViewById(R.id.week_table_day_5);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(selectItemListener);

        // Суббота
        start += dayDuration;
        events = Common.calendarProvider.getAvilableEventsForPeriod(this.getActivity(), start, start + dayDuration);
        adapter = new WeekListAdapter(this.getActivity(), events);
        lv = (ListView)rootView.findViewById(R.id.week_table_day_6);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(selectItemListener);

        // Воскресенье
        start += dayDuration;
        events = Common.calendarProvider.getAvilableEventsForPeriod(this.getActivity(), start, start + dayDuration);
        adapter = new WeekListAdapter(this.getActivity(), events);
        lv = (ListView)rootView.findViewById(R.id.week_table_day_7);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(selectItemListener);

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
