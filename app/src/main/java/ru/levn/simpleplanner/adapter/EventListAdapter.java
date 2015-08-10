package ru.levn.simpleplanner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.levn.simpleplanner.R;
import ru.levn.simpleplanner.calendar.CalendarProvider;
import ru.levn.simpleplanner.calendar.Event;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 27.07.2015.
 */
public class EventListAdapter extends BaseAdapter {

    private LayoutInflater lInflater;
    private ArrayList<Event> eventList;

    public EventListAdapter(Context context, ArrayList<Event> events) {
        eventList = events;
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Object getItem(int position) {
        return eventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = lInflater.inflate(R.layout.event_representation, parent, false);

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
