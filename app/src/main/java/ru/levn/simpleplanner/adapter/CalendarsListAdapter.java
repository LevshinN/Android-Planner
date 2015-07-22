package ru.levn.simpleplanner.adapter;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import ru.levn.simpleplanner.Common;
import ru.levn.simpleplanner.R;

/**
 * Created by Levshin_N on 14.07.2015.
 */
public class CalendarsListAdapter extends BaseAdapter {

    private Context ctx;
    private LayoutInflater lInflater;
    private Map<String, String> objects;
    private Map<String, Boolean> selectedObjects;

    public CalendarsListAdapter(Context context, Map<String, String> calendars, Map<String, Boolean> selectedCalendars) {
        ctx = context;
        objects = calendars;
        selectedObjects = selectedCalendars;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // количество элементов
    @Override
    public int getCount() {
        return objects.size();
    }


    // Получить элемент по позиции
    @Override
    public Object getItem(int position) {
        int i = 0;
        for (Map.Entry<String, String> val : objects.entrySet()) {
            if (i == position) {
                return val;
            }
            i ++;
        }
        return null;
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

        Map.Entry<String, String> val = (Map.Entry<String, String>)getItem(position);


        ((TextView) view.findViewById(R.id.calendar_name)).setText(val.getValue());
        ((TextView) view.findViewById(R.id.calendar_id)).setText(val.getKey());
        ((CheckBox) view.findViewById(R.id.is_calendar_enabled)).setChecked(Common.selectedCalendarsIDs.get(val.getKey()));
        return view;
    }

}
