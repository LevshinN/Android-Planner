package ru.levn.simpleplanner.calendar;

import java.text.SimpleDateFormat;
import java.util.Locale;

import ru.levn.simpleplanner.Common;

/**
 * Created by Levshin_N on 27.07.2015.
 */

public class Event {
    public String EVENT_ID;
    public String CAL_ID;
    public String ORIGINAL_ID;
    public int COLOR;
    public String TITLE;
    public String DESCRIPTION;
    public long DT_START;
    public long DT_END;
    public long DURATION;
    public boolean ALL_DAY;
    public String EVENT_LOC;

    public String getTextDate(boolean withDate) {
        String timeText = "";

        if (withDate) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            timeText += dateFormat.format(DT_START);
            timeText += ", ";
        }

        if (ALL_DAY) {
            timeText += "ALL_DAY";
        } else {
            timeText += CalendarProvider.getTime(DT_START);

            if (DT_END != 0) {
                timeText += " - " + CalendarProvider.getTime(DT_END);
            } else if (DURATION != 0) {
                timeText += " - " + CalendarProvider.getTime(DT_START + DURATION);
            }
        }

        return timeText;
    }
}


