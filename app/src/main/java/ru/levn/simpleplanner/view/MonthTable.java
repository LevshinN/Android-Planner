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
 * Дата создания: 23.08.2015.
 */
public class MonthTable {
    public enum CellMode {CM_CIRCLE, CM_LINE };

    public CellMode cellMode = CellMode.CM_LINE;

    private DayCell[] cells;

    private Calendar representTime;


    private View cellView;
    private int cellWidth;
    private int cellHeight;
    private int screenUp;

    private int cellActiveColor;
    private int cellPassiveColor;
    private int cellPressedBackground;
    private int cellReleasedBackground;
    private int weekColor;

    public int lineSize;

    private Pair<Integer, Integer> touchedCell;

    // Размер окна, в месяцах, в рамках которого подгружаются события
    public static final int EVENTS_WINDOW = 3;

    // Размеры таблицы
    public static final int ROWS = 6;
    public static final int COLUMNS = 8;

    public MonthTable(Context context, Calendar time) {
        cellView = LayoutInflater.from(context).inflate(R.layout.cell_day, null);
        ViewHolder vh = new ViewHolder();
        vh.number = (TextView)cellView.findViewById(R.id.number);
        vh.pieChart = (DayPieChart)cellView.findViewById(R.id.pie_chart);
        vh.lines = (DayLines)cellView.findViewById(R.id.lines);
        cellView.setTag(vh);

        cellActiveColor = context.getResources().getColor(android.R.color.black);
        cellPassiveColor = context.getResources().getColor(android.R.color.darker_gray);
        weekColor = context.getResources().getColor(R.color.red);
        cellPressedBackground = context.getResources().getColor(R.color.yellow);
        cellReleasedBackground = context.getResources().getColor(android.R.color.transparent);

        representTime = (Calendar)time.clone();
        representTime.set(Calendar.DAY_OF_MONTH, 15);
        representTime.getTimeInMillis();
    }

    public void initializeTable(float measureWidth, float measureHeight){

        cellWidth = (int)(measureWidth / COLUMNS);
        cellHeight = (int)(measureHeight / ROWS);

        lineSize = cellHeight;

        cells = new DayCell[COLUMNS * ROWS];

        for (int i = 0; i < ROWS; ++i) {
            for (int j = 0; j < COLUMNS; ++j) {
                DayCell cell = new DayCell();
                if (j == 0) cell.isWeekNumber = true;
                cell.setBounds(cellWidth * j, cellWidth * ( j + 1 ),
                        cellHeight * i, cellHeight * ( i + 1 ));
                cells[ i * COLUMNS + j ] = cell;
            }
        }
        updateCells();
    }

    public void updateCells() {
        Calendar cal = (Calendar)representTime.clone();

        int currentMonth = cal.get(Calendar.MONTH);

        cal.add(Calendar.WEEK_OF_YEAR, -2);
        cal.getTimeInMillis();

        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        cal.getTimeInMillis();

        for (int i = 0; i < ROWS; ++i) {
            for (int j = 0; j < COLUMNS; ++j) {
                cells[ i * COLUMNS + j].representTime = (Calendar)cal.clone();
                cells[ i * COLUMNS + j].isThisMonth = (currentMonth == cal.get(Calendar.MONTH));

                if(j != 0) {
                    cells[i * COLUMNS + j].events = Common.sEvents.
                            getDayEvents(cal.getTimeInMillis());
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    cal.getTimeInMillis();
                }
            }
        }
    }

    public void draw(Canvas canvas) {
        for (DayCell cell : cells) {
            mDrawSingleCell(canvas, cell);
        }
    }

    private void mDrawSingleCell(Canvas canvas, DayCell cell) {
        View v = cellView;

        ViewHolder vh = (ViewHolder)v.getTag();
        vh.number.setText(String.valueOf(cell.getNumber()));

        if (cell.isWeekNumber) {
            vh.number.setTextColor(weekColor);
            v.setBackgroundColor(cellReleasedBackground);
        } else {
            vh.number.setTextColor(cell.isThisMonth ? cellActiveColor : cellPassiveColor);
            v.setBackgroundColor(cell.pressed ? cellPressedBackground : cellReleasedBackground);
        }

        switch (cellMode) {
            case CM_CIRCLE:
                vh.pieChart.setEvents(cell.events);
                break;
            case CM_LINE:
                vh.lines.setEvents(cell.events);
                break;
        }


        int widthSpec = View.MeasureSpec.makeMeasureSpec(cellWidth, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(cellHeight, View.MeasureSpec.EXACTLY);

        v.measure(widthSpec, heightSpec);
        v.layout(0, 0, cellWidth, cellHeight);

        canvas.save();
        canvas.translate((int) cell.startX, (int) cell.startY);

        v.draw(canvas);

        canvas.restore();
    }

    public boolean touchItem(float x, float y) {
        touchedCell = locateTouchedKey(x, y + screenUp);
        if (touchedCell.first < COLUMNS && touchedCell.second < ROWS &&
                touchedCell.first >= 0 && touchedCell.second >= 0) {
            cells[touchedCell.first + touchedCell.second * COLUMNS].pressed = true;
//            Common.sCurrentMode = Common.DAY_MODE;
//            Common.sSelectedDate.
//                    setDate(cells[touchedCell.first + touchedCell.second * COLUMNS].representTime);
//            Common.onUpdate();
            return true;
        }
        return false;
    }

    private Pair<Integer, Integer> locateTouchedKey(float x, float y) {
        int column = (int)(x / cellWidth);
        int row = (int)(y / cellHeight);
        return new Pair<>(column, row);
    }

    public boolean releaseTouch() {
        if ( touchedCell.first < COLUMNS && touchedCell.second < ROWS &&
                touchedCell.first >= 0 && touchedCell.second >= 0) {
            cells[touchedCell.first + touchedCell.second * COLUMNS].pressed = false;
            touchedCell = new Pair<>(COLUMNS, ROWS);
            return true;
        }
        return false;
    }



    public void scrollWeek(int offset) {
        representTime.add(Calendar.WEEK_OF_YEAR, offset);
        representTime.getTimeInMillis();
        Common.sSelectedDate.setDate(representTime);
        Common.sUpdateTitle();
        updateCells();
    }


    static class ViewHolder {
        TextView number;
        DayPieChart pieChart;
        DayLines lines;
    }
}
