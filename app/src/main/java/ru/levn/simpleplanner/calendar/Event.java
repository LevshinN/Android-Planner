package ru.levn.simpleplanner.calendar;

import java.text.SimpleDateFormat;
import java.util.Locale;

import ru.levn.simpleplanner.Common;
import ru.levn.simpleplanner.R;

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
    public long timeOriginalStart;  // Какое время начала сохранено в базе событий
    public long timeOriginalEnd;    // Какое время конца сохранено в базе событий
    public long timeStart;  // Во сколько начинается событие по факту
    public long timeEnd;    // Во сколько заканчивается событие по факту
    public long duration;
    public boolean isAllDay;
    public String timeZone;
    public String location;
    public String rrule;
    public String rdate;
    public String exrule;
    public String exdate;
    public long originalInstanceTime;

    public Event() {}

    public Event (Event e) {
        id = e.id;
        calendarId = e.calendarId;
        originalId = e.originalId;

        color = e.color;
        title = e.title;
        description = e.description;
        location = e.location;

        isAllDay = e.isAllDay;

        timeZone = e.timeZone;

        timeStart = e.timeStart;
        timeEnd = e.timeEnd;

        timeOriginalStart = e.timeOriginalStart;
        timeOriginalEnd = e.timeOriginalEnd;

        duration = e.duration;

        rrule = e.rrule;

        originalInstanceTime = e.originalInstanceTime;
    }

    public String getTextDate(boolean withDate) {
        String timeText = "";

        if (withDate) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            timeText += dateFormat.format(timeStart);
            timeText += ", ";
        }

        if (isAllDay) {
            timeText += Common.sGetResources().getString(R.string.all_day);
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


