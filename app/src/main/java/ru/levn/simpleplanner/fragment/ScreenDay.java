package ru.levn.simpleplanner.fragment;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import ru.levn.simpleplanner.R;
import ru.levn.simpleplanner.adapter.EventListAdapter;
import ru.levn.simpleplanner.calendar.CalendarProvider;
import ru.levn.simpleplanner.calendar.Event;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 14.07.2015.
 */
public class ScreenDay extends Fragment {

    private View mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mRootView = inflater.inflate(R.layout.day, container, false);

        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Pair<Long,Long> period = CalendarProvider.getDayPeriod();

        ArrayList<Event> events = CalendarProvider.getAvilableEventsForPeriod(this.getActivity(), period.first, period.second);
        EventListAdapter eventListAdapter = new EventListAdapter(this.getActivity(), events);

        ListView eventList = (ListView)mRootView.findViewById(R.id.day_event_list);
        eventList.setAdapter(eventListAdapter);
        eventList.setOnItemClickListener(selectItemListener);

        Toast.makeText(this.getActivity(), "" + events.size(), Toast.LENGTH_SHORT).show();
    }

    AdapterView.OnItemClickListener selectItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Event event = (Event)parent.getItemAtPosition(position);
            DialogFragment event_info = EventInfo.newInstance(0, event);
            event_info.show(getFragmentManager(), "event_info");
        }
    };


}
