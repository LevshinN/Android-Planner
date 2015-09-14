package ru.levn.simpleplanner.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

import ru.levn.simpleplanner.calendar.Event;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 27.08.2015.
 */

public class DayPieChart extends View {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF rectF = new RectF();
    private int[] event_colors;
    private int angle;

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

        Object[] arrayEvents = events.toArray();

        event_colors = new int[events.size()];
        for(int i=0; i< events.size(); i++) {
            event_colors[i] = ((Event)arrayEvents[i]).color;
        }
        angle = 360 / events.size();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (event_colors == null) {
            return;
        }

        int xPos = (int)getX();
        int yPos = (int)getY();

        int width = getWidth();
        int height = getHeight();

        int centerX = width / 2;
        int centerY = height / 2;

        int radius = Math.min(width, height) / 2;

        rectF.set(xPos + centerX - radius,
                yPos + centerY - radius,
                xPos + centerX + radius,
                yPos + centerY + radius);


        int delimer = 6;

        if (angle == 360) {
            delimer = 0;
        }

        int temp = 0;
        for (int event_color : event_colors) {

            paint.setColor(0xff000000 + event_color);
            canvas.drawArc(rectF, temp - 90 + delimer / 2, angle - delimer / 2, true, paint);
            temp += angle;
        }

        radius = radius / 3 * 2;
        rectF.set(xPos + centerX - radius,
                yPos + centerY - radius,
                xPos + centerX + radius,
                yPos + centerY + radius);

        paint.setColor(0xffffffff);
        canvas.drawArc(rectF, 0, 360, true, paint);

    }
}
