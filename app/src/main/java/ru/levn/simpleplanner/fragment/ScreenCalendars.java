package ru.levn.simpleplanner.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import ru.levn.simpleplanner.Common;
import ru.levn.simpleplanner.R;
import ru.levn.simpleplanner.adapter.CalendarsListAdapter;

/**
 * Created by Levshin_N on 14.07.2015.
 */
public class ScreenCalendars extends ListFragment {

    public ScreenCalendars() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.calendar_list, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayList<String> calendarsNames = new ArrayList<String>();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            calendarsNames = bundle.getStringArrayList(Common.CALENDARS_LIST_KEY);
        }
        else {
            Toast.makeText(this.getActivity(), "Ooops1", Toast.LENGTH_SHORT).show();
        }

        ListView lv = this.getListView();

        // «аполн€ем лист названи€ми календарей
        CalendarsListAdapter mListAdapter = new CalendarsListAdapter(this.getActivity(), calendarsNames);
        Toast.makeText(this.getActivity(), "" + mListAdapter.getCount(), Toast.LENGTH_SHORT).show();
        lv.setAdapter(mListAdapter);

    }
}
