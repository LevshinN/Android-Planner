package ru.levn.simpleplanner.fragment;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import ru.levn.simpleplanner.R;
import ru.levn.simpleplanner.calendar.Event;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 30.07.2015.
 */
public class EventInfo extends DialogFragment implements View.OnClickListener {

    private Event mEvent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.event_info, container, false);

        mEditTitle(v.findViewById(R.id.event_info_title_area));
        mEditBody(v.findViewById(R.id.event_info_body));

        return v;
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
        }

        ((TextView)v.findViewById(R.id.event_info_title)).setText(mEvent.title);
    }

    private void mEditBody(View v) {
        TextView time = (TextView)v.findViewById(R.id.event_info_time);
        TextView location = (TextView)v.findViewById(R.id.event_info_location);
        TextView description = (TextView)v.findViewById(R.id.event_info_description);

        String timeDescription = mEvent.getTextDate(true);
        if (timeDescription.equals(" ")) {
            ((ViewGroup) time.getParent()).removeView(time);
        } else { time.setText(timeDescription); }

        if (mEvent.location == null || mEvent.location.equals("")) {
            ((ViewGroup) location.getParent()).removeView(location);
        } else { location.setText(mEvent.location); }

        if (mEvent.description == null) {
            ((ViewGroup) description.getParent()).removeView(description);
        } else { description.setText(mEvent.description); }
    }
}
