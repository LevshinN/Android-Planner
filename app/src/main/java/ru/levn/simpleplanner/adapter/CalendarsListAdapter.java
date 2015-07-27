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

        ((TextView) view.findViewById(R.id.calendar_id)).setText(cal.id);
        ((TextView) view.findViewById(R.id.calendar_acc_name)).setText(cal.account_name);
        ((TextView) view.findViewById(R.id.calendar_disp_name)).setText(cal.display_name);
        ((TextView) view.findViewById(R.id.calendar_owner_acc)).setText(cal.owner_account);

        CheckBox isEnabledCB = (CheckBox) view.findViewById(R.id.is_calendar_enabled);
        isEnabledCB.setChecked(cal.enabled);

        isEnabledCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String calID = (String) v.getTag();
                boolean isEnabled = ( (CheckBox) v ).isChecked();
                CalendarProvider.changeCalendarSelection(calID, isEnabled);
            }
        });

        isEnabledCB.setTag(cal.id);

        return view;
    }

}
