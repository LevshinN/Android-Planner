package ru.levn.simpleplanner.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Calendar;

import ru.levn.simpleplanner.Common;
import ru.levn.simpleplanner.R;
import ru.levn.simpleplanner.calendar.Event;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 10.09.2015.
 */
public class WeekLine {
    // Границы расположения текущей клетки
    float startX;
    float startY;
    float endX;
    float endY;

    int currentMonth;

    protected int cellActiveColor;
    protected int cellPassiveColor;
    protected int weekColor;
    protected int backgroundColor;
    protected int pressedBackgroundColor;
    protected int numberFontSize;

    protected float weekNumberCellWidth;
    protected float dayCellWidth;
    protected float height;
    protected float width;

    protected float textHeight;

    Calendar representTime;

    protected ArrayList<Event> events;

    int touchedCell = -1;

    protected Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public WeekLine(Context context) {

        cellActiveColor = context.getResources().getColor(android.R.color.black);
        cellPassiveColor = context.getResources().getColor(android.R.color.darker_gray);
        weekColor = context.getResources().getColor(R.color.red);
        backgroundColor = context.getResources().getColor(R.color.btn_background);
        pressedBackgroundColor = context.getResources().getColor(R.color.btn_pressed_background);
        numberFontSize = context.getResources().getDimensionPixelSize(R.dimen.abc_text_size_body_1_material);
    }

    void setBounds(float startX, float endX, float startY, float endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;

        /*
        С целью ускорения отрисовки не будем делать адаптивную верску а
        просто  зададим ширину клеток. Так как дней в неделе 7 и ещё одна клетка
        для обозначения недели, которая должна быть значительно меньше, то пусть
        их соотношение длинн будет 3:1, тогда делим всю длинну на 7х3+1=22 кусочка.
         */

        width = endX - startX;
        height = endY - startY;

        weekNumberCellWidth = width / 22;
        dayCellWidth = weekNumberCellWidth * 3;

        paint.setTextSize(numberFontSize);
        textHeight = paint.descent() - paint.ascent();
    }

    void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public void draw(Canvas canvas) {

        canvas.save();
        canvas.translate((int) startX, (int) startY);
        {
            drawTable(canvas);
            drawContent(canvas);
            drawNumbers(canvas);
        }
        canvas.restore();
    }

    private void drawTable(Canvas canvas) {

        // Рисуем горизонтальную разделительную линию
        paint.setColor(cellActiveColor);
        canvas.drawLine(weekNumberCellWidth, 0, width, 0, paint);

        // Закрашиваем клетку, до которой докоснулись
        if (touchedCell >= 0 && touchedCell < 7) {
            paint.setColor(pressedBackgroundColor);
            RectF rectF = new RectF(
                    weekNumberCellWidth + touchedCell * dayCellWidth,
                    0,
                    weekNumberCellWidth + ( touchedCell + 1 ) * dayCellWidth,
                    height
            );
            canvas.drawRect(rectF, paint);
        }
    }

    protected void drawNumbers(Canvas canvas) {
        String number = String.valueOf(representTime.get(Calendar.WEEK_OF_YEAR));

        paint.setColor(weekColor);

        int xPos = (int)(weekNumberCellWidth - paint.measureText(number)) / 2;
        int yPos = (int) (height / 2 - (paint.descent() + paint.ascent()) / 2) ;

        canvas.drawText(number, xPos, yPos, paint);

        Calendar c = (Calendar)representTime.clone();

        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        c.getTimeInMillis();

        for (int i = 0; i < 7; ++i) {

            if (c.get(Calendar.MONTH) == currentMonth) {
                paint.setColor(cellActiveColor);
            } else {
                paint.setColor(cellPassiveColor);
            }

            number = String.valueOf(c.get(Calendar.DAY_OF_MONTH));

            xPos = (int)(weekNumberCellWidth + i * dayCellWidth + dayCellWidth / 2 - paint.measureText(number) / 2);

            canvas.drawText(number, xPos, yPos,paint);

            c.add(Calendar.DAY_OF_YEAR, 1);
            c.getTimeInMillis();
        }
    }

    protected void drawContent(Canvas canvas) {}

    public void touchCell(float x) {
        touchedCell = (int)((x - weekNumberCellWidth) / dayCellWidth);
        if (touchedCell < 0 || touchedCell > 6) {
            touchedCell = -1;
        }
    }

    public void releaseCell() {
        touchedCell = -1;
    }

    public void selectItem(float x) {
        int selectedCell = (int)((x - weekNumberCellWidth) / dayCellWidth);
        if (selectedCell >= 0 && selectedCell <= 6) {
            Common.sCurrentMode = Common.DAY_MODE;
            Calendar c = (Calendar)representTime.clone();
            c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            c.getTimeInMillis();
            c.add(Calendar.DAY_OF_YEAR, selectedCell);
            c.getTimeInMillis();
            Common.sSelectedDate.setDate(c);
            Common.onUpdate();
        }
    }
}
