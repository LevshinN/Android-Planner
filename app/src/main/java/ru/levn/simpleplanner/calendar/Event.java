package ru.levn.simpleplanner.calendar;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 27.07.2015.
 */

public class Event {
    public String id;
    public String calendarId;
    public String originalId;
    public int color;
    public String title;
    public String description;
    public long timeStart;
    public long timeEnd;
    public long duration;
    public boolean isAllDay;
    public String location;

    public String getTextDate(boolean withDate) {
        String timeText = "";

        if (withDate) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            timeText += dateFormat.format(timeStart);
            timeText += ", ";
        }

        if (isAllDay) {
            timeText += "ALL_DAY";
        } else {
            timeText += CalendarProvider.getTime(timeStart);

            if (timeEnd != 0) {
                timeText += " - " + CalendarProvider.getTime(timeEnd);
            } else if (duration != 0) {
                timeText += " - " + CalendarProvider.getTime(timeStart + duration);
            }
        }

        return timeText;
    }
}


