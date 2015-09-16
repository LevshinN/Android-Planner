package ru.levn.simpleplanner.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.OverScroller;
import android.widget.Toast;

import ru.levn.simpleplanner.Common;
import ru.levn.simpleplanner.R;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 22.07.2015.
 */

public class MonthView extends View {

    protected boolean measurementChanged = false;
    protected MonthTable monthTable;

    private GestureDetector gestureDetector;
    protected OverScroller scroller;
    protected int yOffset;

    protected int canvasHeight;

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
        monthTable = new MonthTable(context, Common.sSelectedDate.getDate(), attrs);
    }

    // Конструктор, необходимый для наполнения элемента из файла с ресурсом
    public MonthView (Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);
        init();
        monthTable = new MonthTable(context, Common.sSelectedDate.getDate(), attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (isInEditMode()) return;

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
            scroller = new OverScroller(getContext(), new DecelerateInterpolator(10f));
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

        public boolean onSingleTapUp(MotionEvent e) {
            resetTouchFeedback();
            monthTable.selectItem(e.getX(), e.getY());
            return super.onSingleTapUp(e);
        }

        public void onLongPress(MotionEvent e) {
            resetTouchFeedback();
        }

        public boolean onDoubleTapEvent(MotionEvent e) {
            resetTouchFeedback();
            return true;
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
