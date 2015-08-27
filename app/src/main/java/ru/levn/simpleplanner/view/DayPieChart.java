package ru.levn.simpleplanner.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

import ru.levn.simpleplanner.Common;
import ru.levn.simpleplanner.calendar.Event;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 27.08.2015.
 */

public class DayPieChart extends View {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int[] event_colors;
    private int angle;
    RectF rectf = new RectF (0, 0, 30, 30);

    public DayPieChart(Context context) {
        super(context);
    }

    // Конструктор, необходимый для наполнения элемента из файла с ресурсом
    public DayPieChart (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // Конструктор, необходимый для наполнения элемента из файла с ресурсом
    public DayPieChart (Context context, AttributeSet ats, int defaultStyle) {
        super(context, ats, defaultStyle );
    }

    public void setEvents(ArrayList<Event> events) {
        if (events == null || events.size() == 0) {
            event_colors = null;
            return;
        }
        event_colors = new int[events.size()];
        for(int i=0; i< events.size(); i++) {
            event_colors[i] = events.get(i).color;
        }
        angle = 360 / events.size();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (event_colors == null) {
            return;
        }

        int temp = 0;
        for (int i = 0; i < event_colors.length; i++) {

            paint.setColor(event_colors[i]);
            canvas.drawArc(rectf, temp, temp + angle, true, paint);
            temp += angle;
        }
    }
}
