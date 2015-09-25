package ru.levn.simpleplanner.planner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;

import ru.levn.simpleplanner.Common;
import ru.levn.simpleplanner.R;
import ru.levn.simpleplanner.calendar.Event;
import ru.levn.simpleplanner.view.WeekLine;
import ru.levn.simpleplanner.view.WeekLineCircles;
import ru.levn.simpleplanner.view.WeekLineRect;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 24.09.2015.
 */
public class PlannerTable {

    private int scaleMultiplicity = 15;
    private int dateWriterInterval = 1;
    private static final float linesVisibleNum = 8.0f;
    private float linesNum;

    private float lineHeight;
    private float lineWidth;

    public float tableMinHeight;
    public float tableHeight;

    private float horizontalMargin;

    private int lineColor;
    private int frameColor;
    private int textColor;
    private int backgroundColor;

    private ArrayList<EventBox> boxes;
    private int maxColumns = 0;
    float boxWidth;
    float boxMargin = 5.0f;

    float dateWidth;
    float dateHeight;
    float dateFullHeight;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private TextPaint textPaint= new TextPaint();
    RectF boxRect = new RectF();
    public boolean isDrawn = false;

    public long representTime;

    public void initializeTable(float measureWidth, float measureHeight, Context context){
        linesNum = 1440 / scaleMultiplicity;
        lineHeight = measureHeight / linesVisibleNum;
        lineWidth = measureWidth;
        tableHeight = lineHeight * linesNum;
        tableMinHeight = measureHeight;

        horizontalMargin = lineWidth / 30;

        lineColor = context.getResources().getColor(R.color.month_table_divider_color);
        textColor = context.getResources().getColor(android.R.color.black);

        textPaint.setColor(textColor);
        textPaint.setTextSize(lineHeight / 4);

        dateWidth = textPaint.measureText("00:00");
        dateHeight = textPaint.descent() - textPaint.ascent();
    }

    public int getHeight() {
        return (int)(lineHeight * linesNum);
    }

    public void draw(Canvas canvas){
        for (int i = 0; i < linesNum; i += dateWriterInterval) {
            drawSingleLine(canvas, i);
        }

        boxWidth = (lineWidth - dateWidth) / (maxColumns + 1);

        for (EventBox box : boxes) {
            drawBox(canvas, box);
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

        String time = String.format("%02d:%02d", hour, minute);
        int yPos = (int) ( orderNum * lineHeight - textPaint.ascent() - textPaint.descent()) ;
        canvas.drawText(time, 0, yPos, textPaint);
    }

    private void drawBox(Canvas canvas, EventBox box) {
        paint.setColor(box.color + 0xff000000);

        if (!box.isLast) {
            boxRect.set(dateWidth + boxWidth * box.columnNum + boxMargin,
                    box.yStart * lineHeight / scaleMultiplicity + boxMargin,
                    dateWidth + boxWidth * box.columnNum + boxWidth - boxMargin,
                    box.yEnd * lineHeight / scaleMultiplicity - boxMargin);

        } else {
            boxRect.set(dateWidth + boxWidth * box.columnNum + boxMargin,
                    box.yStart * lineHeight / scaleMultiplicity + boxMargin,
                    lineWidth - boxMargin,
                    box.yEnd * lineHeight / scaleMultiplicity - boxMargin);
        }

        canvas.drawRect(boxRect, paint);

        StaticLayout sl = new StaticLayout(box.text, textPaint, (int)boxWidth, Layout.Alignment.ALIGN_NORMAL, 1, 1, false);
        int clipRestoreCount = canvas.save();
        canvas.clipRect(boxRect);

        canvas.translate(dateWidth + boxWidth * box.columnNum + boxMargin, box.yStart * lineHeight / scaleMultiplicity + boxMargin);
        sl.draw(canvas);
        canvas.restoreToCount(clipRestoreCount);
    }

    public void updateEvents() {
        ArrayList<Event> events = Common.sEvents.getDayEvents(representTime);
        boxes = new ArrayList<>();
        maxColumns = 0;

        Calendar c = new GregorianCalendar();
        c.setTimeInMillis(representTime);
        int currDay = c.get(Calendar.DAY_OF_YEAR);

        for (Event e : events) {
            if (e.timeStart >= e.timeEnd) continue;

            int yStart;
            int yEnd;

            if (e.isAllDay) {
                yStart = 0;
                yEnd = 1440;
            } else {
                c.setTimeInMillis(e.timeStart);
                if (c.get(Calendar.DAY_OF_YEAR) != currDay) {
                    yStart = 0;
                } else {
                    yStart = c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE);
                }

                c.setTimeInMillis(e.timeEnd);
                if (c.get(Calendar.DAY_OF_YEAR) != currDay) {
                    yEnd = 1440;
                } else {
                    yEnd = c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE);
                }
            }

            EventBox currBox = new EventBox(yStart, yEnd, e.id, e.title, e.color);

            int column = 0;

            boolean isAdded = false;
            while(!isAdded) {
                boolean isGoodLevel = true;
                for (EventBox box : boxes) {
                    if (box.columnNum == column && (
                            !(box.yStart >= currBox.yEnd || box.yEnd <= currBox.yStart))) {
                        box.isLast = false;
                        isGoodLevel = false;
                        break;
                    }
                }
                if (isGoodLevel) {
                    currBox.columnNum = column;
                    boxes.add(currBox);
                    isAdded = true;
                } else {
                    column += 1;
                    if (column > maxColumns) maxColumns = column;
                }
            }
        }
    }

    public void scale(float scaleMultiplier) {
        lineHeight *= scaleMultiplier;
        if (lineHeight > tableMinHeight) lineHeight = tableMinHeight;
        if (lineHeight * linesNum < tableMinHeight) {
            lineHeight = tableMinHeight / linesNum;
        }
        tableHeight = linesNum * lineHeight;

        if (dateHeight > lineHeight * dateWriterInterval) {
            dateWriterInterval *= 2;
            return;
        }

        if (dateHeight * 2 < lineHeight && dateWriterInterval > 1) {
            dateWriterInterval /= 2;
        }
    }
}
