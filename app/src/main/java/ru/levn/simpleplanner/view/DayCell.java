package ru.levn.simpleplanner.view;

import java.util.ArrayList;
import java.util.Calendar;

import ru.levn.simpleplanner.calendar.Event;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 23.08.2015.
 */
public class DayCell {

    // Границы расположения текущей клетки
    float startX;
    float startY;
    float endX;
    float endY;

    boolean pressed = false;
    boolean isThisMonth = true;
    boolean isWeekNumber = false;

    Calendar representTime;

    ArrayList<Event> events;

    void setBounds(float startX, float endX, float startY, float endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    int getNumber() {
        if (isWeekNumber) {
            return representTime.get(Calendar.WEEK_OF_YEAR);
        } else {
            return representTime.get(Calendar.DAY_OF_MONTH);
        }
    }


}
