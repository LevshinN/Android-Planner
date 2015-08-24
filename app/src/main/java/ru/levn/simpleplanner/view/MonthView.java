package ru.levn.simpleplanner.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.OverScroller;

import java.util.ArrayList;

import ru.levn.simpleplanner.Common;
import ru.levn.simpleplanner.calendar.Event;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 22.07.2015.
 */

public class MonthView extends View {

    private boolean measurementChanged = false;

    private MonthTable monthTable;

    private GestureDetector gestureDetector;

    private OverScroller scroller;

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
        if (scroller.computeScrollOffset()) {
            yOffset = scroller.getCurrY();
        }

        if (measurementChanged) {
            measurementChanged = false;
            monthTable.initializeTable(getMeasuredWidth(), getMeasuredHeight());
        }

        if ( yOffset > monthTable.lineSize || yOffset < -monthTable.lineSize ) {
            monthTable.scrollWeek(yOffset / monthTable.lineSize);
            yOffset = yOffset % monthTable.lineSize;
        }

        canvas.save();
        canvas.translate(0, -yOffset);

        monthTable.updateBounds(yOffset, canvasHeight + yOffset);
        monthTable.draw(canvas);

        canvas.restore();

        if (!scroller.isFinished()) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void init() {
        if (!isInEditMode()) {
            scroller = new OverScroller(getContext());
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

            invalidate();

            return true;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            int minY;
            int maxY;
            int speedY;

            if (velocityY > 0) {
                minY = yOffset - monthTable.lineSize * 10;
                maxY = yOffset;
                speedY = -monthTable.lineSize;
            } else {
                minY = yOffset;
                maxY = yOffset + monthTable.lineSize * 10;
                speedY = monthTable.lineSize * 3;
            }

            scroller.fling(0, yOffset, 0, speedY, 0, 0, minY, maxY );
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
