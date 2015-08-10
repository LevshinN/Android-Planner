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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ru.levn.simpleplanner.R;
import ru.levn.simpleplanner.adapter.EventAdapter;
import ru.levn.simpleplanner.calendar.CalendarProvider;
import ru.levn.simpleplanner.calendar.Event;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 14.07.2015.
 */
public class ScreenDay extends Fragment {

    private View mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mRootView = inflater.inflate(R.layout.day, container, false);

        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Pair<Long,Long> period = CalendarProvider.getDayPeriod();

        ArrayList<Event> events = CalendarProvider.getAvilableEventsForPeriod(this.getActivity(), period.first, period.second);
        EventDayAdapter eventListAdapter = new EventDayAdapter(this.getActivity(), events);

        ListView eventList = (ListView)mRootView.findViewById(R.id.day_event_list);
        eventList.setAdapter(eventListAdapter);
        eventList.setOnItemClickListener(selectItemListener);

        Toast.makeText(this.getActivity(), "" + events.size(), Toast.LENGTH_SHORT).show();
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

class EventDayAdapter extends EventAdapter {

    public EventDayAdapter(Context context, ArrayList<Event> events) {
        mEventList = events;
        mLInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = mLInflater.inflate(R.layout.event_representation, parent, false);

        Event event = (Event) getItem(position);


        editContentView(view.findViewById(R.id.event_content), event);
        editColorView(view.findViewById(R.id.event_color), event.color);
        editTimeView(view.findViewById(R.id.time_view), event);

        return view;
    }

    private void editContentView(View v, Event event) {

        TextView id = (TextView)v.findViewById(R.id.event_id);
        TextView title = (TextView)v.findViewById(R.id.event_title);
        TextView description = (TextView)v.findViewById(R.id.event_description);
        TextView location = (TextView)v.findViewById(R.id.event_location_description);
        View locView = v.findViewById(R.id.event_location);

        id.setText(event.id);

        if ( event.title == null ) {
            ((ViewGroup) title.getParent()).removeView(title);
        } else {
            title.setText(event.title);
        }

        if ( event.description == null ) {
            ((ViewGroup) description.getParent()).removeView(description);
        } else {
            String clear = event.description.replaceAll("\\s+", "");
            if ( clear.equals("") ) {
                ((ViewGroup) description.getParent()).removeView(description);
            }
            else description.setText(event.description);
        }

        if ( event.location == null || event.location.equals("") ) {
            ((ViewGroup) locView.getParent()).removeView(locView);
        } else {
            location.setText(event.location);
        }
    }

    private void editColorView(View v, int color) {
        if (color != 0) {
            v.setBackgroundColor(0xff000000 + color);
        }
    }

    private void editTimeView(View v, Event event) {
        if ( event.isAllDay || event.timeStart == 0 ) {
            ((ViewGroup) v.getParent()).removeView(v);
        } else {
            ((TextView)v.findViewById(R.id.event_start_time)).setText(CalendarProvider.getTime(event.timeStart));

            if (event.timeEnd != 0) {
                ((TextView)v.findViewById(R.id.event_end_time)).setText(CalendarProvider.getTime(event.timeEnd));
            } else if ( event.duration > 0 ) {
                ((TextView)v.findViewById(R.id.event_end_time)).setText(CalendarProvider.getTime(event.timeStart + event.duration));
            } else {
                TextView tv = (TextView)v.findViewById(R.id.event_end_time);
                ((ViewGroup) tv.getParent()).removeView(tv);
            }
        }
    }
}
