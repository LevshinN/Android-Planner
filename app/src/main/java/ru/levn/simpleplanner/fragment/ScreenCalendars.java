package ru.levn.simpleplanner.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ru.levn.simpleplanner.R;
import ru.levn.simpleplanner.adapter.CalendarAdapter;
import ru.levn.simpleplanner.calendar.CalendarProvider;
import ru.levn.simpleplanner.calendar.MyCalendar;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 14.07.2015.
 */

public class ScreenCalendars extends ModeFragment {

    private ListView mCalendarsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.calendar_list, container, false);
        mCalendarsList = (ListView)rootView.findViewById(R.id.calendars_list);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Заполняем лист названиями календарей
        final CalendarListAdapter mListAdapter = new CalendarListAdapter(this.getActivity(), CalendarProvider.calendars);
        Toast.makeText(this.getActivity(), "" + mListAdapter.getCount(), Toast.LENGTH_SHORT).show();
        mCalendarsList.setAdapter(mListAdapter);
    }

    @Override
    public void onUpdate() {
        //TODO
    }

    @Override
    public void onBuild() {
        // TODO
    }
}

class CalendarListAdapter extends CalendarAdapter {

    public CalendarListAdapter(Context context, ArrayList<MyCalendar> calendars) {
        mCalendarList = calendars;
        mLInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = mLInflater.inflate(R.layout.calendar_representation, parent, false);
        }

        MyCalendar cal = (MyCalendar)getItem(position);

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
                boolean isEnabled = ((CheckBox) v).isChecked();
                CalendarProvider.changeCalendarSelection(calID, isEnabled);
            }
        });

        isEnabledCB.setTag(cal.id);

        return view;
    }
}
