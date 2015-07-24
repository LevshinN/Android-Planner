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
import ru.levn.simpleplanner.calendar.Calendar;
import ru.levn.simpleplanner.calendar.CalendarProvider;

/**
 * Created by Levshin_N on 14.07.2015.
 */
public class CalendarsListAdapter extends BaseAdapter {

    private Context ctx;
    private LayoutInflater lInflater;
    private ArrayList<Calendar> calendarList;

    public CalendarsListAdapter(Context context, ArrayList<Calendar> calendars) {
        calendarList = calendars;
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // количество элементов
    @Override
    public int getCount() {
        return calendarList.size();
    }


    // Получить элемент по позиции
    @Override
    public Object getItem(int position) {
        return calendarList.get(position);
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

        Calendar cal = (Calendar)getItem(position);

        ((TextView) view.findViewById(R.id.calendar_name)).setText(cal.getName());
        ((TextView) view.findViewById(R.id.calendar_id)).setText(cal.getId());

        CheckBox isEnabledCB = (CheckBox) view.findViewById(R.id.is_calendar_enabled);
        isEnabledCB.setChecked(cal.isEnabled());

        isEnabledCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String calID = (String) v.getTag();
                boolean isEnabled = ( (CheckBox) v ).isChecked();
                CalendarProvider.changeCalendarSelection(calID, isEnabled);
            }
        });

        isEnabledCB.setTag(cal.getId());

        return view;
    }

}
