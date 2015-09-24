package ru.levn.simpleplanner.planner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.OverScroller;
import android.widget.Scroller;

import java.util.Calendar;

import ru.levn.simpleplanner.R;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 24.09.2015.
 */
public class PlannerView extends View {

    protected boolean measurementChanged = false;
    private PlannerTable table;
    private int tableHeight;
    private int tableWidth;

    private int yOffset;
    private GestureDetector gestureDetector;
    private OverScroller scroller;

    public PlannerView(Context context) {
        super(context);
        init();
    }

    private void init() {
        table = new PlannerTable();
        gestureDetector = new GestureDetector(getContext(), gestureListener);
        scroller = new OverScroller(getContext());
    }

    protected void onDraw(Canvas canvas) {
        if (scroller.computeScrollOffset()) {
            yOffset = scroller.getCurrY();
        }

        if (measurementChanged) {
            measurementChanged = false;
            table.initializeTable(getMeasuredWidth(), getMeasuredHeight(), getContext());
            tableHeight = table.getHeight();
        }

        canvas.save();
        canvas.translate(0, -yOffset);

        table.draw(canvas);

        canvas.restore();

        if (!scroller.isFinished()) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        tableWidth = MeasureSpec.getSize(widthMeasureSpec);
        measurementChanged = true;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event) || gestureDetector.onTouchEvent(event);
    }

    private GestureDetector.OnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {

        public boolean onDown(MotionEvent e) {
            return true;
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            yOffset += distanceY;

            if (yOffset < 0) yOffset = 0;
            if (yOffset > tableHeight - getMeasuredHeight()) {
                yOffset = tableHeight - getMeasuredHeight();
            }

            invalidate();

            return true;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            scroller.fling(0, yOffset, 0, (int) -velocityY, 0, 0, 0, tableHeight - getMeasuredHeight());

            return true;
        }
    };
}
