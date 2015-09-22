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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import ru.levn.simpleplanner.Common;
import ru.levn.simpleplanner.R;
import ru.levn.simpleplanner.adapter.EventAdapter;
import ru.levn.simpleplanner.calendar.CalendarProvider;
import ru.levn.simpleplanner.calendar.Event;


/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 14.08.2015.
 *
 * Фрагмент, соответствующий одной странице ViewFlipper`а
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

        View mRootView = inflater.inflate(R.layout.day_page, container, false);

        ArrayList<Event> mEvents = Common.sEvents.getDayEvents(representTime);

        if (mEvents.isEmpty()) {
            mRootView.findViewById(R.id.day_text_info).setVisibility(View.VISIBLE);
        }

        EventDayAdapter mAdapter = new EventDayAdapter(this.getActivity(), mEvents, representTime);

        ListView eventList = (ListView)mRootView.findViewById(R.id.day_event_list);
        eventList.setAdapter(mAdapter);
        eventList.setOnItemClickListener(selectItemListener);

        if (mRootView.findViewById(R.id.day_info_box) != null) {

            TextView number = (TextView) mRootView.findViewById(R.id.day_info_number);
            TextView month = (TextView) mRootView.findViewById(R.id.day_info_month);
            TextView weekDay = (TextView) mRootView.findViewById(R.id.day_info_week_day);

            SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.getDefault());
            number.setText(sdf.format(representTime));

            sdf = new SimpleDateFormat("MMMM", Locale.getDefault());
            month.setText(sdf.format(representTime));

            sdf = new SimpleDateFormat("EEE", Locale.getDefault());
            weekDay.setText(sdf.format(representTime).toUpperCase());
        }

        return mRootView;
    }
}

class EventDayAdapter extends EventAdapter {

    long mRepresentTime;

    public EventDayAdapter(Context context, ArrayList<Event> events, long representTime) {
        mEventList = events;
        mLInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRepresentTime = representTime;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = mLInflater.inflate(R.layout.day_event, parent, false);
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


        if ( event.title != null && event.title.length() != 0 ) {
            title.setText(cutString(event.title));
            String letter = String.valueOf(event.title.toUpperCase().charAt(0));
            firstLetter.setText(letter);
        } else title.setText("");


        description.setText(cutString(event.description));
        location.setText(cutString(event.location));
    }

    private void editColorView(View v, int color) {
        GradientDrawable bgShape = (GradientDrawable) v.getBackground();
        if (color != 0) {
            bgShape.setColor(0xff000000 + color);
        } else {
            bgShape.setColor(v.getContext().getResources().getColor(R.color.default_color));
        }
    }

    private void editTimeView(View v, Event event) {
        TextView timeText1 = (TextView)v.findViewById(R.id.event_time_1);
        TextView timeText2 = (TextView)v.findViewById(R.id.event_time_2);

        // Будем использовать промежуток выбранного дня, чтобы для событий,
        // которые начинаются раньше текущего дня (заканчиваются позже
        // текущего дня), отображались, писалась дата начала и конца

        Pair<Long,Long> dayPeriod = CalendarProvider.getDayPeriod(mRepresentTime, false);

        if ( event.isAllDay || event.timeStart == 0 ) {
            v.setLayoutParams(new LinearLayout.LayoutParams(0,0));
        } else {
            v.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            long timeStart = event.timeStart;
            long timeEnd;

            if (event.timeEnd != 0) {
                timeEnd = event.timeEnd;
            } else {
                timeEnd = event.timeStart + event.duration;
            }

            // Если событие начинается раньше выбранного дня
            if (timeStart < dayPeriod.first) {
                timeStart = dayPeriod.first;
            }


            // Если событие заканчивается позже выбранного дняж
            if (timeEnd >= dayPeriod.second) {
                timeEnd = dayPeriod.second - 1;
            }

            // Если событие происходит на протяжении всего дня
            if (timeStart == dayPeriod.first && timeEnd == dayPeriod.second - 1) {
                v.setLayoutParams(new LinearLayout.LayoutParams(0,0));
            } else {
                v.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

                timeText1.setText(CalendarProvider.getTime(timeStart));
                timeText2.setText(CalendarProvider.getTime(timeEnd));

            }
        }
    }

    private String cutString( String original ) {
        if (original != null) {
            String[] lines = original.split("\n");
            if (lines.length != 0) {
                return lines[0];
            } else {
                return null;
            }
        } else return null;
    }
}
