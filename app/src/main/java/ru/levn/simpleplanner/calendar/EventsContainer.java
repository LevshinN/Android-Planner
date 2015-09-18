package ru.levn.simpleplanner.calendar;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 24.08.2015.
 */
public class EventsContainer {
    private long mUtcTimeStart;
    private long mUtcTimeEnd;

    private int mWeekWindow;

    private ArrayList<Event> mEvents;

    public EventsContainer(long utcRepresentTime, int weekWindow) {

        Pair<Long, Long> boreders = mGetBorders(utcRepresentTime, weekWindow);
        mWeekWindow = weekWindow;
        mUtcTimeStart = boreders.first;
        mUtcTimeEnd = boreders.second;
        mEvents = CalendarProvider.getAvilableEventsForPeriod(mUtcTimeStart, mUtcTimeEnd);
    }

    public void update() {
        mEvents = CalendarProvider.getAvilableEventsForPeriod(mUtcTimeStart, mUtcTimeEnd);
    }

    public void update(long utcRepresentTime, int weekWindow) {

        Pair<Long, Long> newBorders = mGetBorders(utcRepresentTime, weekWindow);

        mEvents = CalendarProvider.getAvilableEventsForPeriod(newBorders.first, newBorders.second);

        mUtcTimeStart = newBorders.first;
        mUtcTimeEnd = newBorders.second;
        mWeekWindow = weekWindow;
    }

    private Pair<Long, Long> mGetBorders (long utcRepresentTime, int weekWindow) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(utcRepresentTime);
        calendar.getTimeInMillis();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        calendar.getTimeInMillis();

        calendar.add(Calendar.WEEK_OF_YEAR, -weekWindow);
        long utcTimeStart = calendar.getTimeInMillis();

        calendar.add(Calendar.WEEK_OF_YEAR, 2 * weekWindow);
        long utcTimeEnd = calendar.getTimeInMillis();

        return new Pair<>(utcTimeStart, utcTimeEnd);
    }

    public ArrayList<Event> get(long start, long end) {

        if(start < mUtcTimeStart || end > mUtcTimeEnd) {
            update((start + end) / 2, mWeekWindow);
        }

        ArrayList<Event> targetEvents = new ArrayList<>();
        for (Event event : mEvents) {
            if (event.timeStart < end && event.timeEnd > start) {
                Event e = new Event(event);
                //if (event.timeStart < start) e.timeStart = start;
                //if (event.timeEnd > end) e.timeEnd = end;

                if (e.timeStart <= start && e.timeEnd >= end) {
                    e.isAllDay = true;
                }

                targetEvents.add(e);
            }
        }
        return targetEvents;
    }

    public ArrayList<Event> getDayEvents(long time) {
        Pair<Long, Long> borders = CalendarProvider.getDayPeriod(time, false);
        Pair<Long, Long> utcBorders = CalendarProvider.getDayPeriod(time,true);

        ArrayList<Event> allEvents = get(borders.first, borders.second);

        // Среди полученнх событий могут встречаться те, которые на весь
        // день и, из-за сдвига временных зон, захватывают 2 дня в другой временной зоне.
        // Отбросим такие события.

        return mFilterEvents(allEvents ,utcBorders);
    }

    public ArrayList<Event> getWeekEvents(long time) {
        Pair<Long, Long> borders = CalendarProvider.getWeekPeriod(time, false);
        Pair<Long, Long> utcBorders = CalendarProvider.getWeekPeriod(time, true);

        ArrayList<Event> allEvents = get(borders.first, borders.second);

        // Среди полученнх событий могут встречаться те, которые на весь
        // день и, из-за сдвига временных зон, захватывают 2 дня в другой временной зоне.
        // Отбросим такие события.

        return mFilterEvents(allEvents ,utcBorders);
    }

    private ArrayList<Event> mFilterEvents( ArrayList<Event> events, Pair<Long, Long> utcBorders ) {

        ArrayList<Event> clearEvents = new ArrayList<>();

        for (Event e : events) {
            if (e.isAllDay) {
                if (e.timeStart == e.timeEnd && e.timeStart < utcBorders.second) {
                    clearEvents.add(e);
                    continue;
                }

                if (e.timeStart < utcBorders.second && e.timeEnd > utcBorders.first) {
                    clearEvents.add(e);
                }
            } else {
                clearEvents.add(e);
            }
        }
        return clearEvents;
    }

    public void deleteEvent(Event e, boolean all) {
        ArrayList<Event> clearEvents = new ArrayList<>();
        for (Event event : mEvents) {
            if (!event.id.equals(e.id)) {
                clearEvents.add(event);
            } else {
                if (!all && event.timeStart != e.timeStart) {
                    clearEvents.add(event);
                }
            }
        }
        mEvents = clearEvents;
    }
}
