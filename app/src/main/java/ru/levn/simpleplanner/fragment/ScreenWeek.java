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
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ru.levn.simpleplanner.Common;
import ru.levn.simpleplanner.R;
import ru.levn.simpleplanner.adapter.EventAdapter;
import ru.levn.simpleplanner.calendar.CalendarProvider;
import ru.levn.simpleplanner.calendar.Event;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 14.07.2015.
 */

public class ScreenWeek extends Fragment {

    private View mRootView;
    private long mCalendarDate;
    private CalendarView mCalendarView;

    private int[] mDayViewIds = {
            R.id.week_table_day_1,  // Понедельник
            R.id.week_table_day_2,  // Вторник
            R.id.week_table_day_3,  // Среда
            R.id.week_table_day_4,  // Четверг
            R.id.week_table_day_5,  // Пятница
            R.id.week_table_day_6,  // Суббота
            R.id.week_table_day_7,  // Воскресенье
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.week, container, false);

        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRefreshView();
    }

    AdapterView.OnItemClickListener mSelectItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Event event = (Event)parent.getItemAtPosition(position);
            EventInfo event_info = EventInfo.newInstance(0, event);
            event_info.show(getFragmentManager(), "event_info");
        }
    };

    private void mRefreshView() {

        Pair<Long,Long> period = CalendarProvider.getWeekPeriod();

        long start = period.first;
        long end = period.second;
        long dayDuration = (end - start) / 7;

        for (int i = 0; i < 7; ++i) {
            ArrayList<Event> events = CalendarProvider.getDayEvents(start);
            EventWeekAdapter adapter = new EventWeekAdapter(this.getActivity(), events);
            ListView lv = (ListView)mRootView.findViewById(mDayViewIds[i]);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(mSelectItemListener);
            start += dayDuration;
        }
    }
}

class EventWeekAdapter extends EventAdapter {
    public EventWeekAdapter(Context context, ArrayList<Event> events) {
        mEventList = events;
        mLInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = mLInflater.inflate(R.layout.event_small_representation, parent, false);
        }

        Event event = (Event) getItem(position);

        if (event.color != 0) {
            (view.findViewById(R.id.event_small_area)).setBackgroundColor(0xff000000 + event.color);
        }

        ((TextView)view.findViewById(R.id.event_info_title)).setText(event.title + " " + event.id);
        ((TextView)view.findViewById(R.id.event_info_time)).setText(event.getTextDate(false));

        return view;
    }
}
