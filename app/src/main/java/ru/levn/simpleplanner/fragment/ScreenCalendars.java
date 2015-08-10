package ru.levn.simpleplanner.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import ru.levn.simpleplanner.R;
import ru.levn.simpleplanner.adapter.CalendarsListAdapter;
import ru.levn.simpleplanner.calendar.CalendarProvider;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 14.07.2015.
 */

public class ScreenCalendars extends Fragment {

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
        final CalendarsListAdapter mListAdapter = new CalendarsListAdapter(this.getActivity(), CalendarProvider.calendars);
        Toast.makeText(this.getActivity(), "" + mListAdapter.getCount(), Toast.LENGTH_SHORT).show();
        mCalendarsList.setAdapter(mListAdapter);
    }
}
