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

    public Calendar representTime;

    private float lineWidth;
    private float lineHeight;
    private int screenUp;

    private Context context;

    public int lineSize;

    private int touchedLine = -1;

    private int mContentMode = -1;

    // Таблица представляет из себя цикличный список, который изменяется в
    // зависимости от того, как пользователь скроллит. listStart указывет,
    // где у этого списка начало.
    protected int listStart = 0;

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

            lines[i] = line;
        }

        updateLines(0);
    }

    public void updateLines( int offset ) {

        int newListStart = (listStart + offset + ROWS) % ROWS;
        int currentMonth = representTime.get(Calendar.MONTH);

        if (offset < 0) {
            Calendar cal = (Calendar)lines[newListStart].representTime.clone();
            cal.add(Calendar.WEEK_OF_YEAR, -ROWS);
            cal.getTimeInMillis();
            for(int i = newListStart; i != listStart; i = (i + 1) % ROWS) {
                lines[i].representTime = (Calendar)cal.clone();
                lines[i].currentMonth = currentMonth;
                lines[i].setEvents(Common.sEvents.getWeekEvents(cal.getTimeInMillis()));
                cal.add(Calendar.WEEK_OF_YEAR, 1);
                cal.getTimeInMillis();
            }
        } else if (offset > 0) {
            Calendar cal = (Calendar)lines[listStart].representTime.clone();
            cal.add(Calendar.WEEK_OF_YEAR, ROWS);
            cal.getTimeInMillis();
            for(int i = listStart; i != newListStart; i = (i + 1) % ROWS) {
                lines[i].representTime = (Calendar)cal.clone();
                lines[i].currentMonth = currentMonth;
                lines[i].setEvents(Common.sEvents.getWeekEvents(cal.getTimeInMillis()));
                cal.add(Calendar.WEEK_OF_YEAR, 1);
                cal.getTimeInMillis();
            }
        } else {
            Calendar cal = (Calendar)representTime.clone();
            cal.add(Calendar.WEEK_OF_YEAR, -ROWS / 2);
            cal.getTimeInMillis();
            for(int i = listStart; i < listStart + ROWS; i += 1) {
                lines[i % ROWS].representTime = (Calendar)cal.clone();
                lines[i % ROWS].currentMonth = currentMonth;
                lines[i % ROWS].setEvents(Common.sEvents.getWeekEvents(cal.getTimeInMillis()));
                cal.add(Calendar.WEEK_OF_YEAR, 1);
                cal.getTimeInMillis();
            }
        }
        listStart = newListStart;

        for (int i = 0; i < ROWS; ++i) {
            lines[(i + listStart) % ROWS].setBounds(0, lineWidth, lineHeight * (i - 1), lineHeight * i);
            lines[(i + listStart) % ROWS].currentMonth = currentMonth;
        }
    }

    public void draw(Canvas canvas) {
        for (int i = listStart; i < listStart + ROWS; ++i) {
            mDrawSingleLine(canvas, lines[i % ROWS]);
        }
    }

    private void mDrawSingleLine(Canvas canvas, WeekLine line) {
        line.draw(canvas);
    }

    public void scrollWeek(int offset) {
        touchedLine = (touchedLine - offset) % ROWS;
        representTime.add(Calendar.WEEK_OF_YEAR, offset);
        representTime.getTimeInMillis();
        Common.sUpdateTitle(representTime);
        updateLines(offset);
    }

    public boolean touchItem(float x, float y) {
        touchedLine = locateTouchedLine(y + screenUp);
        lines[touchedLine].touchCell(x);
        return true;
    }

    public Calendar selectItem(float x, float y) {
        int selectedLine = locateTouchedLine(y + screenUp);
        return lines[selectedLine].selectItem(x);
    }

    public boolean releaseTouch() {
        if (touchedLine >= 0 && touchedLine < ROWS )
            lines[touchedLine].releaseCell();
        return true;
    }

    private int locateTouchedLine(float y) {
        int row = (int)(y / lineHeight);
        return (row + listStart + 1 + ROWS) % ROWS;
    }

    public void updateBounds(int up, int down) {
        screenUp = up;
    }

}
