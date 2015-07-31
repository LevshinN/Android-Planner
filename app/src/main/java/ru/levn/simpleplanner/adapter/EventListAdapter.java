package ru.levn.simpleplanner.adapter;

import android.content.Context;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import ru.levn.simpleplanner.R;
import ru.levn.simpleplanner.calendar.Event;

/**
 * Created by Levshin_N on 27.07.2015.
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
        editColorView(view.findViewById(R.id.event_color), event.COLOR);
        editTimeView(view.findViewById(R.id.time_view), event);

        return view;
    }

    private void editContentView(View v, Event event) {

        TextView id = (TextView)v.findViewById(R.id.event_id);
        TextView title = (TextView)v.findViewById(R.id.event_title);
        TextView description = (TextView)v.findViewById(R.id.event_description);
        TextView location = (TextView)v.findViewById(R.id.event_location_description);
        View locView = v.findViewById(R.id.event_location);

        id.setText(event.EVENT_ID);

        if ( event.TITLE == null ) {
            ((ViewGroup) title.getParent()).removeView(title);
        } else {
            title.setText(event.TITLE);
        }

        if ( event.DESCRIPTION == null ) {
            ((ViewGroup) description.getParent()).removeView(description);
        } else {
            String clear = event.DESCRIPTION.replaceAll("\\s+", "");
            if ( clear.equals("") ) {
                ((ViewGroup) description.getParent()).removeView(description);
            }
            else description.setText(event.DESCRIPTION);
        }

        if ( event.EVENT_LOC == null || event.EVENT_LOC.equals("") ) {
            ((ViewGroup) locView.getParent()).removeView(locView);
        } else {
            location.setText(event.EVENT_LOC);
        }
    }

    private void editColorView(View v, int color) {
        if (color == 0) {
            ((ViewGroup) v.getParent()).removeView(v);
        }

        v.setBackgroundColor(0xff000000 + color);
    }

    private void editTimeView(View v, Event event) {
        if ( event.ALL_DAY || event.DT_START == 0 ) {
            ((ViewGroup) v.getParent()).removeView(v);
        } else {
            ((TextView)v.findViewById(R.id.event_start_time)).setText(getTime(event.DT_START));

            if (event.DT_END != 0) {
                ((TextView)v.findViewById(R.id.event_end_time)).setText(getTime(event.DT_END));
            } else if ( event.DURATION > 0 ) {
                ((TextView)v.findViewById(R.id.event_end_time)).setText(getTime(event.DT_START + event.DURATION));
            } else {
                TextView tv = (TextView)v.findViewById(R.id.event_end_time);
                ((ViewGroup) tv.getParent()).removeView(tv);
            }
        }
    }

    private String getTime(long UTCTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTimeInMillis(UTCTime);

        return String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    }
}
