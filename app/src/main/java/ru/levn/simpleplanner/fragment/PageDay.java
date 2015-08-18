package ru.levn.simpleplanner.fragment;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import ru.levn.simpleplanner.R;
import ru.levn.simpleplanner.adapter.EventAdapter;
import ru.levn.simpleplanner.calendar.CalendarProvider;
import ru.levn.simpleplanner.calendar.Event;


/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 14.08.2015.
 */
public class PageDay extends Fragment {

    static final String ARGUMENT_REPRESENT_DATE = "arg_represent_date";

    long representTime;

    public boolean changed;

    public static PageDay newInstance(long date) {
        PageDay pageDay = new PageDay();
        Bundle arguments = new Bundle();
        arguments.putLong(ARGUMENT_REPRESENT_DATE, date);
        pageDay.setArguments(arguments);
        return pageDay;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        representTime = getArguments().getLong(ARGUMENT_REPRESENT_DATE);
        changed = false;
    }

    AdapterView.OnItemClickListener selectItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Event event = (Event)parent.getItemAtPosition(position);
            EventInfo event_info = EventInfo.newInstance(0, event);
            event_info.show(getFragmentManager(), "event_info");
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //mRootView = inflater.inflate(R.layout.day_page, container, false);
        View mRootView = inflater.inflate(R.layout.day_page, container, false);

        Pair<Long,Long> period = CalendarProvider.getDayPeriod(representTime);

        ArrayList<Event> mEvents = CalendarProvider.getAvilableEventsForPeriod(period.first, period.second);

        if (mEvents.isEmpty()) {
            mRootView.findViewById(R.id.day_text_info).setVisibility(View.VISIBLE);
        }

        EventDayAdapter mAdapter = new EventDayAdapter(this.getActivity(), mEvents);

        ListView eventList = (ListView)mRootView.findViewById(R.id.day_event_list);
        eventList.setAdapter(mAdapter);
        eventList.setOnItemClickListener(selectItemListener);

        return mRootView;
    }
}

class EventDayAdapter extends EventAdapter {

    public EventDayAdapter(Context context, ArrayList<Event> events) {
        mEventList = events;
        mLInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = mLInflater.inflate(R.layout.event_representation, parent, false);
        }

        Event event = (Event) getItem(position);

        editContentView(view, event);
        editColorView(view.findViewById(R.id.event_color), event.color);
        editTimeView(view.findViewById(R.id.time_view), event);

        return view;
    }

    private void editContentView(View v, Event event) {

        TextView title = (TextView)v.findViewById(R.id.event_title);
        TextView description = (TextView)v.findViewById(R.id.event_description);
        TextView firstLetter = (TextView)v.findViewById(R.id.event_letter);
        TextView location = (TextView)v.findViewById(R.id.event_location);


        if ( event.title != null ) {
            title.setText(cutString(event.title));
            String letter = String.valueOf(event.title.toUpperCase().charAt(0));
            firstLetter.setText(letter);
        } else title.setText("");


        description.setText(cutString(event.description));
        location.setText(cutString(event.location));
    }

    private void editColorView(View v, int color) {
        if (color != 0) {
            GradientDrawable bgShape = (GradientDrawable)v.getBackground();
            bgShape.setColor(0xff000000 + color);
        }
    }

    private void editTimeView(View v, Event event) {
        TextView timeText1 = (TextView)v.findViewById(R.id.event_time_1);
        TextView timeText2 = (TextView)v.findViewById(R.id.event_time_2);

        // Будем использовать промежуток выбранного дня, чтобы для событий,
        // которые начинаются раньше текущего дня (заканчиваются позже
        // текущего дня), отображались, писалась дата начала и конца

        Pair<Long,Long> dayPeriod = CalendarProvider.getDayPeriod(Calendar.getInstance().getTimeInMillis());

        if ( event.isAllDay || event.timeStart == 0 ) {
            timeText1.setText("");
            timeText2.setText("ALL DAY");
        } else {

            long timeEnd;
            if (event.timeEnd != 0) {
                timeEnd = event.timeEnd;
            } else {
                timeEnd = event.timeStart + event.duration;
            }

            // Если событие происходит не только внутри текущего периода
            if (event.timeStart < dayPeriod.first || event.timeEnd > dayPeriod.second) {

                // Если событие начинается раньше или позже текущего периода
                if ( event.timeStart < dayPeriod.first || event.timeStart > dayPeriod.second ) {
                    timeText1.setText(CalendarProvider.getDate(event.timeStart)
                            + ", " + CalendarProvider.getTime(event.timeStart));
                } else {
                    timeText1.setText("Today"
                            + ", " + CalendarProvider.getTime(event.timeStart));
                }

                // Если событие заканчивается позже или раньше текущего периода
                if ( timeEnd > dayPeriod.second || timeEnd < dayPeriod.first ) {
                    timeText2.setText(CalendarProvider.getDate(timeEnd)
                            + ", " + CalendarProvider.getTime(timeEnd));
                } else {
                    timeText2.setText("Today"
                            + ", " + CalendarProvider.getTime(timeEnd));
                }
            } else {
                // Когда всё событие происходит внутри рассматриваемого периода
                timeText1.setText("Today");
                timeText2.setText(CalendarProvider.getTime(event.timeStart)
                        + " - " + CalendarProvider.getTime(timeEnd));
            }
        }
    }

    private String cutString( String original ) {
        if (original != null) {
            String[] lines = original.split("\n");
            if (lines.length != 0) {
                return lines[0];
            } else {
                return "";
            }
        } else return "";
    }
}
