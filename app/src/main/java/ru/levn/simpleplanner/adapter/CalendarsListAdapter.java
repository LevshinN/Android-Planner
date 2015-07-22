package ru.levn.simpleplanner.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.levn.simpleplanner.R;

/**
 * Created by Levshin_N on 14.07.2015.
 */
public class CalendarsListAdapter extends BaseAdapter {

    Context ctx;
    LayoutInflater lInflater;
    ArrayList<String> objects;

    public CalendarsListAdapter(Context context, ArrayList<String> calendars) {
        ctx = context;
        objects = calendars;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // number of elements
    @Override
    public int getCount() {
        return objects.size();
    }


    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("CalendarsListAdapter", "getView is called");


        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.calendar_representation, parent, false);
        }

        String name = (String)getItem(position);


        ((TextView) view.findViewById(R.id.calendar_name)).setText(name);
        return view;
    }

}
