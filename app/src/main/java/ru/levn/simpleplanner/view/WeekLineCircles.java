package ru.levn.simpleplanner.view;

import android.content.Context;
import android.graphics.Canvas;

import java.util.Calendar;

import ru.levn.simpleplanner.Common;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 14.09.2015.
 */
public class WeekLineCircles extends WeekLine {

    DayPieChart pieChartPattern;
    int circleMargin = 5;

    public WeekLineCircles(Context context) {
        super(context);

        pieChartPattern = new DayPieChart(context);
    }

    @Override
    protected void drawContent(Canvas canvas) {

        Calendar c = (Calendar)representTime.clone();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        c.getTimeInMillis();

        for (int i = 0; i < 7; ++i) {
            pieChartPattern.setEvents(Common.sEvents.getDayEvents(c.getTimeInMillis()));
            pieChartPattern.measure(
                    (int) dayCellWidth - circleMargin * 2,
                    (int) height - circleMargin * 2);

            pieChartPattern.layout(
                    (int)(weekNumberCellWidth + i * dayCellWidth + circleMargin),
                    circleMargin,
                    (int)(weekNumberCellWidth + ( i + 1 ) * dayCellWidth - circleMargin),
                    (int)(height - circleMargin)
            );
            pieChartPattern.draw(canvas);
            c.add(Calendar.DAY_OF_WEEK , 1);
        }
    }
}
