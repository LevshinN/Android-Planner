package ru.levn.simpleplanner.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.levn.simpleplanner.Common;
import ru.levn.simpleplanner.calendar.Event;

/**
 * Created by Levshin_N on 22.07.2015.
 */
public class MonthView extends View {

    private boolean measurementChanged = false;

    private MonthTable monthTable;

    private GestureDetector gestureDetector;

    private int yOffset;

    private int canvasHeight;

    // Конструктор, необходимый для создания элемента внутри кода программы
    public MonthView(Context context) {
        super(context);
        init();
        monthTable = new MonthTable(context, Common.sSelectedDate.getDate());
    }

    // Конструктор, необходимый для наполнения элемента из файла с ресурсом
    public MonthView (Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        monthTable = new MonthTable(context, Common.sSelectedDate.getDate());
    }

    // Конструктор, необходимый для наполнения элемента из файла с ресурсом
    public MonthView (Context context, AttributeSet ats, int defaultStyle) {
        super(context, ats, defaultStyle );
        init();
        monthTable = new MonthTable(context, Common.sSelectedDate.getDate());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (measurementChanged) {
            measurementChanged = false;
            monthTable.initializeTable(getMeasuredWidth(), getMeasuredHeight());
        }

        canvas.save();
        canvas.translate(0, -yOffset);

        monthTable.updateBounds(yOffset, canvasHeight + yOffset);
        monthTable.draw(canvas);

        canvas.restore();
    }

    private void init() {
        if (!isInEditMode()) {
            gestureDetector = new GestureDetector(getContext(), gestureListener);
        }
    }

    private GestureDetector.OnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {

        public boolean onDown(MotionEvent e) {
            if (monthTable.touchItem(e.getX(), e.getY())) {
                invalidate();
            }

            return true;
        }


        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            resetTouchFeedback();

            yOffset += distanceY;

            if (yOffset > monthTable.lineSize) {
                monthTable.scrollWeek(yOffset / monthTable.lineSize);
                yOffset = yOffset % monthTable.lineSize;
            }

            if (yOffset < -monthTable.lineSize) {
                monthTable.scrollWeek(yOffset / monthTable.lineSize);
                yOffset = yOffset % monthTable.lineSize;
            }

            invalidate();

            return true;
        }

        public boolean onSingleTapUp(MotionEvent e) {
            resetTouchFeedback();
            return super.onSingleTapUp(e);
        }

    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_CANCEL) {
            resetTouchFeedback();
        }
        return super.onTouchEvent(event) || gestureDetector.onTouchEvent(event);
    }

    private void resetTouchFeedback() {
        if (monthTable.releaseTouch()) {
            invalidate();
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        canvasHeight = MeasureSpec.getSize(heightMeasureSpec);
        measurementChanged = true;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }




}
