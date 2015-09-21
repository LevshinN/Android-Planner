package ru.levn.simpleplanner.fragment;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

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
        final CalendarListAdapter mListAdapter = new CalendarListAdapter(this.getActivity(),
                CalendarProvider.getAllSortedCalendars());
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

class CalendarListAdapter extends BaseAdapter {

    final private ArrayList<ArrayList<MyCalendar>> mCalendarGroups;
    private LayoutInflater mLInflater;

    private View mCalendarItem;

    public CalendarListAdapter(Context context, ArrayList<ArrayList<MyCalendar>> groups) {
        mCalendarGroups = groups;
        mLInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mCalendarItem  = mLInflater.inflate(R.layout.calendar_item, null);
    }

    @Override
    public int getCount() {
        return mCalendarGroups.size();
    }

    @Override
    public Object getItem(int position) {
        return mCalendarGroups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = mLInflater.inflate(R.layout.calendar_group, parent, false);
        }

        ArrayList<MyCalendar> calendars = (ArrayList<MyCalendar>)getItem(position);

        ((TextView) view.findViewById(R.id.calendar_acc_box_name)).setText(calendars.get(0).accountName);
        LinearLayout calendarList = (LinearLayout)view.findViewById(R.id.calendar_list_box);
        int listSize = calendarList.getChildCount();
        if (listSize > calendars.size()) {
            for (int i = calendars.size(); i < listSize; ++i) {
                calendarList.getChildAt(i).setVisibility(View.GONE);
            }
        } else if (calendars.size() > listSize){
            for (int i = listSize; i < calendars.size(); ++i) {
                View item = mLInflater.inflate(R.layout.calendar_item, calendarList, false);
                calendarList.addView(item);
            }
        }

        for (int i = 0; i < calendars.size(); ++i) {
            ((CheckBox)calendarList.getChildAt(i)).setText(calendars.get(i).displayName);
            ((CheckBox)calendarList.getChildAt(i)).setChecked(calendars.get(i).enabled);
            ((CheckBox)calendarList.getChildAt(i)).setTag(calendars.get(i).id);
            ((CheckBox)calendarList.getChildAt(i)).setVisibility(View.VISIBLE);
            ((CheckBox)calendarList.getChildAt(i)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String calID = (String) v.getTag();
                    boolean isEnabled = ((CheckBox) v).isChecked();
                    CalendarProvider.changeCalendarSelection(calID, isEnabled);
                }
            });
        }

        return view;
    }
}
