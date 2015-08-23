package ru.levn.simpleplanner.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import ru.levn.simpleplanner.R;
import ru.levn.simpleplanner.calendar.CalendarProvider;
import ru.levn.simpleplanner.calendar.Event;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 23.08.2015.
 */
public class MonthTable {

    private DayCell[] cells;
    private ArrayList<Event> events;

    private long timeStart;
    private long timeEnd;

    private Calendar representTime;


    private View cellView;
    private int cellWidth;
    private int cellHeight;

    private int cellActiveColor;
    private int cellPassiveColor;
    private int weekColor;

    // Размер окна, в месяцах, в рамках которого подгружаются события
    public static final int EVENTS_WINDOW = 3;

    // Размеры таблицы
    public static final int ROWS = 6;
    public static final int COLUMNS = 8;

    public MonthTable(Context context, Calendar time) {
        this.cellView = LayoutInflater.from(context).inflate(R.layout.cell_day, null);
        cellActiveColor = context.getResources().getColor(android.R.color.black);
        cellPassiveColor = context.getResources().getColor(R.color.grey);
        weekColor = context.getResources().getColor(R.color.red);
        representTime = (Calendar)time.clone();
        updateEvents();
    }


    private void updateEvents() {
        // Подгружаем в таблицу события на 3 месяца вперед и назад
        Calendar cal = (Calendar)representTime.clone();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        cal.add(Calendar.MONTH, -EVENTS_WINDOW);
        this.timeStart = cal.getTimeInMillis();

        cal.add(Calendar.MONTH, EVENTS_WINDOW * 2);
        this.timeEnd = cal.getTimeInMillis();

        this.events = CalendarProvider.getAvilableEventsForPeriod(this.timeStart, this.timeEnd);
    }

    public void initializeTable(float measureWidth, float measureHeight){
        if (representTime.getTimeInMillis() < this.timeStart
                || representTime.getTimeInMillis() > this.timeEnd) {
            updateEvents();
        }

        Calendar cal = (Calendar)representTime.clone();

        int currentMonth = cal.get(Calendar.MONTH);

        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.getTimeInMillis();

        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        cal.getTimeInMillis();

        cellWidth = (int)(measureWidth / COLUMNS);
        cellHeight = (int)(measureHeight / ROWS);

        cells = new DayCell[COLUMNS * ROWS];
        for (int i = 0; i < ROWS; ++i) {
            for (int j = 0; j < COLUMNS; ++j) {
                DayCell cell = new DayCell();
                if (j == 0) cell.isWeekNumber = true;
                cell.setBounds(cellWidth * j, cellWidth * ( j + 1 ),
                        cellHeight * i, cellHeight * ( i + 1 ));
                cell.representTime = (Calendar)cal.clone();
                cell.isThisMonth = (currentMonth == cal.get(Calendar.MONTH));
                cells[ i * COLUMNS + j ] = cell;
                if(j != 0) {
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    cal.getTimeInMillis();
                }
            }
        }
    }

    public void draw(Canvas canvas) {
        for (int i = 0; i < cells.length; ++i) {
            mDrawSingleCell(canvas, cells[i]);
        }
    }

    private void mDrawSingleCell(Canvas canvas, DayCell cell) {
        View v = cellView;

        TextView number = (TextView)v.findViewById(R.id.number);
        number.setText(String.valueOf(cell.getNumber()));

        if (cell.isWeekNumber) {
            number.setTextColor(weekColor);
        } else {
            if (cell.isThisMonth) {
                number.setTextColor(cellActiveColor);
            } else {
                number.setTextColor(cellPassiveColor);
            }
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

    private void fillCellWithEvents(DayCell cell) {
        for (Event event : events) {
            // TODO
        }
    }


}
