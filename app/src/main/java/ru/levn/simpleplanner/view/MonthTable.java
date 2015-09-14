package ru.levn.simpleplanner.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

import ru.levn.simpleplanner.Common;
import ru.levn.simpleplanner.R;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 10.09.2015.
 */
public class MonthTable {

    private WeekLine[] lines;

    private Calendar representTime;

    private float lineWidth;
    private float lineHeight;
    private int screenUp;

    private Context context;

    public int lineSize;

    private int touchedLine = -1;

    private int mContentMode = -1;

    // Размеры таблицы
    public static final int ROWS = 8;

    public MonthTable(Context context, Calendar time) {

        this.context = context;

        representTime = (Calendar)time.clone();
        representTime.set(Calendar.DAY_OF_MONTH, 15);
        representTime.getTimeInMillis();
    }

    public MonthTable(Context context, Calendar time, AttributeSet attrs) {

        this.context = context;

        representTime = (Calendar)time.clone();
        representTime.set(Calendar.DAY_OF_MONTH, 15);
        representTime.getTimeInMillis();

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.MonthView,
                0, 0);

        try {
            mContentMode = a.getInteger(R.styleable.MonthView_eventRepresentation, -1);
        } finally {
            a.recycle();
        }
    }

    public void initializeTable(float measureWidth, float measureHeight){

        lineWidth = measureWidth;
        lineHeight = measureHeight / (ROWS - 2);

        lineSize = (int)lineHeight;


        lines = new WeekLine[ROWS];
        for (int i = 0; i < ROWS; ++i) {
            WeekLine line;
            switch(mContentMode) {
                case 0:
                    line = new WeekLineRect(context);
                    break;
                case 1:
                    line = new WeekLineCircles(context);
                    break;
                default:
                    line = new WeekLine(context);
            }

            line.setBounds(0, lineWidth, lineHeight * ( i - 1 ), lineHeight * i );
            lines[i] = line;
        }

        updateLines();
    }

    public void updateLines() {
        Calendar cal = (Calendar)representTime.clone();

        int currentMonth = cal.get(Calendar.MONTH);

        cal.add(Calendar.WEEK_OF_YEAR, -3);
        cal.getTimeInMillis();

        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        cal.getTimeInMillis();

        for (int i = 0; i < ROWS; ++i) {
            lines[i].representTime = (Calendar)cal.clone();
            lines[i].currentMonth = currentMonth;
            lines[i].setEvents(Common.sEvents.getWeekEvents(cal.getTimeInMillis()));
            cal.add(Calendar.WEEK_OF_YEAR, 1);
            cal.getTimeInMillis();
        }
    }

    public void draw(Canvas canvas) {
        for (WeekLine line : lines) {
            mDrawSingleLine(canvas, line);
        }
    }

    private void mDrawSingleLine(Canvas canvas, WeekLine line) {
        line.draw(canvas);
    }

    public void scrollWeek(int offset) {
        touchedLine -= offset;
        representTime.add(Calendar.WEEK_OF_YEAR, offset);
        representTime.getTimeInMillis();
        Common.sSelectedDate.setDate(representTime);
        Common.sUpdateTitle();
        updateLines();
    }

    public boolean touchItem(float x, float y) {
        touchedLine = 1 + locateTouchedLine(y + screenUp);
        if (touchedLine < ROWS - 1 && touchedLine > 0) {
            lines[touchedLine].touchCell(x);
            return true;
        }
        return false;
    }

    public void selectItem(float x, float y) {
        int selectedLine = 1 + locateTouchedLine(y + screenUp);
        if (selectedLine < ROWS - 1 && selectedLine > 0) {
            lines[selectedLine].selectItem(x);
        }
    }

    public boolean releaseTouch() {
        lines[touchedLine].releaseCell();
        return true;
    }

    private int locateTouchedLine(float y) {
        int row = (int)(y / lineHeight);
        return row;
    }

    public void updateBounds(int up, int down) {
        screenUp = up;
    }

}
