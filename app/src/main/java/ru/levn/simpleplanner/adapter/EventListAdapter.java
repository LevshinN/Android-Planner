package ru.levn.simpleplanner.adapter;

import android.content.Context;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
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


        editView((TextView) view.findViewById(R.id.event_title), event.TITLE);
        editView((TextView) view.findViewById(R.id.event_description), event.DESCRIPTION);
        editView((TextView) view.findViewById(R.id.event_start_time), getTime(event.DT_START));
        editView((TextView) view.findViewById(R.id.event_end_time), getTime(event.DT_END));
        editView((TextView) view.findViewById(R.id.event_id), event.COLOR);

        if (!editView((TextView) view.findViewById(R.id.event_location), event.EVENT_LOC)) {
            View v = view.findViewById(R.id.location_icon);
            ((ViewGroup) v.getParent()).removeView(v);
        }

        editView((ImageView)view.findViewById(R.id.event_color), event.COLOR);

        return view;
    }

    private boolean editView(TextView v, String text) {
        if ( text == null ) {
            ((ViewGroup) v.getParent()).removeView(v);
            return false;
        } else {

            String clearText = text.replaceAll("\\s+", "");
            if (clearText.equals("")) {
                ((ViewGroup) v.getParent()).removeView(v);
                return false;
            } else {
                v.setText(text);
                return true;
            }
        }
    }

    private boolean editView(ImageView v, String color) {
        if (color == null) {
            return false;
        }

        v.setBackgroundColor(0xff000000 + new Integer(color));
        return true;
    }

    private String getTime(long UTCTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.setTimeInMillis(UTCTime);

        return "" + cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE);
    }
}
