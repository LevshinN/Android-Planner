package ru.levn.simpleplanner.planner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import ru.levn.simpleplanner.R;
import ru.levn.simpleplanner.view.WeekLine;
import ru.levn.simpleplanner.view.WeekLineCircles;
import ru.levn.simpleplanner.view.WeekLineRect;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 24.09.2015.
 */
public class PlannerTable {

    private int scaleMultiplicity = 15;
    private float tableRatio = 3.0f;
    private final int LINES_VISIBLE = 8;

    private float lineHeight;
    private float lineWidth;
    private float horizontalMargin;

    private int lineColor;
    private int frameColor;
    private int textColor;
    private int backgroundColor;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    public boolean isDrawn = false;

    public void initializeTable(float measureWidth, float measureHeight, Context context){
        lineHeight = measureHeight / LINES_VISIBLE;
        lineWidth = measureWidth;
        horizontalMargin = lineWidth / 30;

        lineColor = context.getResources().getColor(R.color.month_table_divider_color);
        textColor = context.getResources().getColor(android.R.color.black);
    }

    public int getHeight() {
        return (int)(lineHeight * 1440 / scaleMultiplicity);
    }

    public void draw(Canvas canvas){
        for (int i = 0; i < 1440 / scaleMultiplicity; ++i) {
            drawSingleLine(canvas, i);
        }
        isDrawn = true;
    }

    private void drawSingleLine(Canvas canvas, int orderNum) {
        int minutesSinceMidnight = orderNum * scaleMultiplicity;
        int hour = minutesSinceMidnight / 60;
        int minute = minutesSinceMidnight % 60;

        paint.setColor(lineColor);
        canvas.drawLine(0 + horizontalMargin,
                orderNum * lineHeight,
                lineWidth - horizontalMargin,
                orderNum * lineHeight,
                paint);

        paint.setColor(textColor);
        paint.setTextSize(lineHeight / 4);
        String time = String.format("%02d:%02d", hour, minute);
        int yPos = (int) ( orderNum * lineHeight + (paint.descent() - paint.ascent())) ;
        canvas.drawText(time, 0, yPos, paint);
    }
}
