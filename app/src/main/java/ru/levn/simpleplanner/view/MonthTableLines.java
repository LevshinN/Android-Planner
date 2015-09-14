package ru.levn.simpleplanner.view;

import android.content.Context;
import android.graphics.Canvas;
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
public class MonthTableLines {

    private WeekLine[] lines;

    private Calendar representTime;

    private View cellView;
    private float lineWidth;
    private float lineHeight;
    private int screenUp;

    private int cellActiveColor;
    private int cellPassiveColor;
    private int weekColor;

    private Context context;

    public int lineSize;

    private int touchedLine = -1;

    // Размеры таблицы
    public static final int ROWS = 8;

    public MonthTableLines(Context context, Calendar time) {

        this.context = context;

        representTime = (Calendar)time.clone();
        representTime.set(Calendar.DAY_OF_MONTH, 15);
        representTime.getTimeInMillis();
    }

    public void initializeTable(float measureWidth, float measureHeight){

        lineWidth = measureWidth;
        lineHeight = measureHeight / (ROWS - 2);

        lineSize = (int)lineHeight;

        lines = new WeekLine[ROWS];

        for (int i = 0; i < ROWS; ++i) {
            WeekLine line = new WeekLine(context);
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
        representTime.add(Calendar.WEEK_OF_YEAR, offset);
        representTime.getTimeInMillis();
        Common.sSelectedDate.setDate(representTime);
        Common.sUpdateTitle();
        updateLines();
    }

    public boolean touchItem(float x, float y) {
        touchedLine = locateTouchedLine(y + screenUp);
        if (touchedLine < ROWS - 2 && touchedLine >= 0) {
            lines[touchedLine + 1].touchCell(x);
//            Common.sCurrentMode = Common.DAY_MODE;
//            Common.sSelectedDate.
//                    setDate(cells[touchedCell.first + touchedCell.second * COLUMNS].representTime);
//            Common.onUpdate();
            return true;
        }
        return false;
    }

    private int locateTouchedLine(float y) {
        int row = (int)(y / lineHeight);
        return row;
    }

    public void updateBounds(int up, int down) {
        screenUp = up;
    }

}
