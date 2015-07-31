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
 * Created by Levshin_N on 30.07.2015.
 */
public class EventInfo extends DialogFragment implements View.OnClickListener {

    private Event event;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.event_info, null);

        editTitle(v.findViewById(R.id.event_info_title_area));
        editBody(v.findViewById(R.id.event_info_body));

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
        event = e;
    }


    private void editTitle(View v) {
        if (event.COLOR != 0) {
            v.setBackgroundColor(0xff000000 + event.COLOR);
        }

        ((TextView)v.findViewById(R.id.event_info_title)).setText(event.TITLE);
    }

    private void editBody(View v) {
        TextView time = (TextView)v.findViewById(R.id.event_info_time);
        TextView location = (TextView)v.findViewById(R.id.event_info_location);
        TextView description = (TextView)v.findViewById(R.id.event_info_description);

        String timeDescription = getTimeDescription();
        if (timeDescription.equals(" ")) {
            ((ViewGroup) time.getParent()).removeView(time);
        } else { time.setText(timeDescription); }

        if (event.EVENT_LOC == null || event.EVENT_LOC.equals("")) {
            ((ViewGroup) location.getParent()).removeView(location);
        } else { location.setText(event.EVENT_LOC); }

        if (event.DESCRIPTION == null) {
            ((ViewGroup) description.getParent()).removeView(description);
        } else { description.setText(event.DESCRIPTION); }
    }

    private String getTimeDescription() {
        return "" + event.DT_START + " " + event.DT_END;
    }

}
