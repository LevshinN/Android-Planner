package ru.levn.simpleplanner.fragment;


import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;

import ru.levn.simpleplanner.Common;
import ru.levn.simpleplanner.R;
import ru.levn.simpleplanner.calendar.CalendarProvider;
import ru.levn.simpleplanner.calendar.Event;
import ru.levn.simpleplanner.calendar.MyCalendar;
import ru.levn.simpleplanner.calendar.RRule;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 30.07.2015.
 */
public class EventInfo extends DialogFragment implements View.OnClickListener {

    private Event mEvent;
    private CardView mButtonEdit;
    private CardView mButtonExit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.event_info, container, false);

        mEditTitle(v.findViewById(R.id.event_info_title));
        mEditBody(v.findViewById(R.id.event_info_body));

        (v.findViewById(R.id.event_info_edit)).setOnClickListener(onClick);
        (v.findViewById(R.id.event_info_close)).setOnClickListener(onClick);

        return v;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(Common.sScreenWidth * 4 / 5, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    public static EventInfo newInstance(int num, Event e) {
        EventInfo ei = new EventInfo();

        Bundle args = new Bundle();
        args.putInt("num", num);
        ei.setArguments(args);
        ei.setEvent(e);

        return ei;
    }

    public void setEvent(Event e) {
        mEvent = e;
    }

    private void mEditTitle(View v) {
        if (mEvent.color != 0) {
            v.setBackgroundColor(0xff000000 + mEvent.color);
        } else {
            v.setBackgroundColor(v.getContext().getResources().getColor(R.color.default_color));
        }

        ((TextView)v).setText(mEvent.title);
    }

    private void mEditBody(View v) {
        mEditTime(v);
        mEditRepeatRule(v);
        mEditLocation(v.findViewById(R.id.event_info_location_line));
        mEditDescription(v.findViewById(R.id.event_info_description_line));
        mEditCalendar(v.findViewById(R.id.event_info_calendar_line));
    }

    private void mEditTime(View v) {
        TextView time = (TextView)v.findViewById(R.id.event_info_time);
        if (mEvent.isAllDay) {
            time.setText(CalendarProvider.getDate(mEvent.timeStart)
                    + ",\n"
                    + CalendarProvider.getDate(mEvent.timeEnd)
                    + ", "
                    + getResources().getString(R.string.all_day));
        } else {
            long timeEnd = mEvent.timeEnd;
            if (timeEnd == 0) {
                timeEnd = mEvent.timeStart + mEvent.duration;
            }

            String timeDescription;
            timeDescription = CalendarProvider.getTime(mEvent.timeStart)
                    + ", "
                    + CalendarProvider.getDate(mEvent.timeStart)
                    + " - \n"
                    + CalendarProvider.getTime(timeEnd) + ", " + CalendarProvider.getDate(timeEnd);

            time.setText(timeDescription);
        }
    }

    private void mEditRepeatRule( View v ) {
        if (mEvent.rrule != null) {
            String ruleDescription;

            RRule rRule = new RRule();
            try {
                rRule.setStart(mEvent.timeStart);
                rRule.parse(mEvent.rrule);
                ruleDescription = rRule.getDescription() + "\n" +
                        "---------------------------------" + "\n" +
                        mEvent.rrule + '\n' +
                        mEvent.rdate + '\n' +
                        mEvent.exrule + '\n' +
                        mEvent.exdate;

                ((TextView)v.findViewById(R.id.event_info_repeat)).setText(ruleDescription);

            } catch (ParseException exception) {
                exception.printStackTrace();
            }

        } else {
            v.findViewById(R.id.event_info_repeat_line)
                    .setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        }

    }

    private void mEditLocation(View v){
        if (mEvent.location == null || mEvent.location.equals("")) {
            v.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        } else {
            ((TextView)v.findViewById(R.id.event_info_location)).setText(mEvent.location);
        }
    }

    private void mEditDescription(View v){
        if (mEvent.description == null || mEvent.description.equals("")) {
            v.setLayoutParams(new LinearLayout.LayoutParams(0,0));
        } else {
            ((TextView)v.findViewById(R.id.event_info_description)).setText(mEvent.description);
        }
    }

    private void mEditCalendar(View v) {
        MyCalendar eventCalendar = null;
        for (MyCalendar c : CalendarProvider.calendars) {
            if (c.id.equals(mEvent.calendarId)) {
                eventCalendar = c;
                break;
            }
        }
        if (eventCalendar != null) {
            ((TextView)v.findViewById(R.id.event_info_calendar)).setText( eventCalendar.displayName
                    + " ("
                    + eventCalendar.accountName
                    + ")" );
        }

    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();

            switch(id) {
                case R.id.event_info_close:
                    dismiss();
                    break;
                case R.id.event_info_edit:
                    android.app.DialogFragment editEventDialog = CreateEventFragment.newInstance(0, mEvent);
                    editEventDialog.show(getActivity().getFragmentManager(), "edit_event");
                    dismiss();
                    break;
            }
        }
    };
}
