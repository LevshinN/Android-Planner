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
 * Created by Levshin_N on 31.07.2015.
 */
public class WeekListAdapter extends BaseAdapter {
    private LayoutInflater lInflater;
    private ArrayList<Event> weekEventList;

    public WeekListAdapter(Context context, ArrayList<Event> events) {
        weekEventList = events;
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return weekEventList.size();
    }

    @Override
    public Object getItem(int position) {
        return weekEventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = lInflater.inflate(R.layout.event_small_representation, parent, false);
        Event event = (Event) getItem(position);

        if (event.COLOR != 0) {
            (view.findViewById(R.id.event_small_area)).setBackgroundColor(0xff000000 + event.COLOR);
        }

        ((TextView)view.findViewById(R.id.event_info_title)).setText(event.TITLE);
        ((TextView)view.findViewById(R.id.event_info_time)).setText(event.getTextDate(false));

        return view;
    }
}
