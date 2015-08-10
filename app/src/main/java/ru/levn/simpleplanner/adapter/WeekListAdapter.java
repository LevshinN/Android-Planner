package ru.levn.simpleplanner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.levn.simpleplanner.R;
import ru.levn.simpleplanner.calendar.Event;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 31.07.2015.
 */

public class WeekListAdapter extends BaseAdapter {
    private LayoutInflater mLInflater;
    private ArrayList<Event> mWeekEventList;

    public WeekListAdapter(Context context, ArrayList<Event> events) {
        mWeekEventList = events;
        mLInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mWeekEventList.size();
    }

    @Override
    public Object getItem(int position) {
        return mWeekEventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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

        ((TextView)view.findViewById(R.id.event_info_title)).setText(event.title);
        ((TextView)view.findViewById(R.id.event_info_time)).setText(event.getTextDate(false));

        return view;
    }
}
