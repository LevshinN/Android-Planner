package ru.levn.simpleplanner.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.levn.simpleplanner.Common;
import ru.levn.simpleplanner.R;
import ru.levn.simpleplanner.adapter.CalendarsListAdapter;
import ru.levn.simpleplanner.calendar.CalendarProvider;

/**
 * Created by Levshin_N on 14.07.2015.
 */
public class ScreenCalendars extends Fragment {

    private static Map<String, Boolean> selectedCalendarIDs;

    private ListView calendarsList;

    public ScreenCalendars() {
        selectedCalendarIDs = new HashMap<String, Boolean>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.calendar_list, container, false);
        calendarsList = (ListView)rootView.findViewById(R.id.calendars_list);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Map<String, Boolean> newSelectedCalendarIDs = new HashMap<String, Boolean>();


        // Заполняем лист названиями календарей
        final CalendarsListAdapter mListAdapter = new CalendarsListAdapter(this.getActivity(), CalendarProvider.GetCalendars(), Common.selectedCalendarsIDs);
        Toast.makeText(this.getActivity(), "" + mListAdapter.getCount(), Toast.LENGTH_SHORT).show();
        calendarsList.setAdapter(mListAdapter);

        calendarsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Получаем список выбраныых элементов
                CheckBox c = (CheckBox)view.findViewById(R.id.is_calendar_enabled);
                Common.selectedCalendarsIDs.put(((Map.Entry<String,String>)mListAdapter.getItem(position)).getKey(), c.isActivated());
            }
        });

    }
}
