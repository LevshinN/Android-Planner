package ru.levn.simpleplanner.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import ru.levn.simpleplanner.R;
import ru.levn.simpleplanner.calendar.Calendar;
import ru.levn.simpleplanner.calendar.CalendarProvider;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 14.07.2015.
 */

public class CalendarsListAdapter extends BaseAdapter {

    private LayoutInflater mLInflater;
    private ArrayList<Calendar> mCalendarList;

    public CalendarsListAdapter(Context context, ArrayList<Calendar> calendars) {
        mCalendarList = calendars;
        mLInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // количество элементов
    @Override
    public int getCount() {
        return mCalendarList.size();
    }


    // Получить элемент по позиции
    @Override
    public Object getItem(int position) {
        return mCalendarList.get(position);
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
            view = mLInflater.inflate(R.layout.calendar_representation, parent, false);
        }

        Calendar cal = (Calendar)getItem(position);

        ((TextView) view.findViewById(R.id.calendar_id)).setText(cal.id);
        ((TextView) view.findViewById(R.id.calendar_acc_name)).setText(cal.accountName);
        ((TextView) view.findViewById(R.id.calendar_disp_name)).setText(cal.displayName);
        ((TextView) view.findViewById(R.id.calendar_owner_acc)).setText(cal.ownerAccount);

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
