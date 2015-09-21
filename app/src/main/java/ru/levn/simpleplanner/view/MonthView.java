package ru.levn.simpleplanner.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.OverScroller;
import android.widget.Scroller;
import android.widget.Toast;

import java.util.Calendar;

import javax.xml.datatype.Duration;

import ru.levn.simpleplanner.Common;
import ru.levn.simpleplanner.R;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 22.07.2015.
 */

public class MonthView extends View {

    protected boolean measurementChanged = false;
    protected MonthTable monthTable;

    protected OnDateSelectedListener mListener;

    private GestureDetector gestureDetector;
    protected Scroller scroller;
    protected int yOffset;
    protected int uncutYOffset;

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
            yOffset += scroller.getCurrY() - uncutYOffset;
            uncutYOffset = scroller.getCurrY();
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
            scroller = new Scroller(getContext(), new DecelerateInterpolator(0.8f));
            gestureDetector = new GestureDetector(getContext(), gestureListener);
        }
    }

    private GestureDetector.OnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {

        public boolean onDown(MotionEvent e) {
            if (monthTable.touchItem(e.getX(), e.getY())) {
                Log.d("TOUCH", "onDown      -   touch");
                invalidate();
            }
            return true;
        }


        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            resetTouchFeedback();
            Log.d("TOUCH", "onScroll    -   release");

            yOffset += distanceY;

            invalidate();

            return true;
        }

        public boolean onSingleTapUp(MotionEvent e) {
            resetTouchFeedback();
            Log.d("TOUCH", "onSingleTap -   release");
            Calendar c = monthTable.selectItem(e.getX(), e.getY());
            if ( c != null ) {
                mListener.onSelect(c);
            }
            return super.onSingleTapUp(e);
        }

        public void onLongPress(MotionEvent e) {
            Log.d("TOUCH", "onLongPress -   release");
            resetTouchFeedback();
        }

        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.d("TOUCH", "onDoubleTap -   release");
            resetTouchFeedback();
            return true;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d("TOUCH", "onFling     -   release");
            resetTouchFeedback();
            uncutYOffset = 0;
            scroller.fling(0, yOffset,
                    0, (int)-velocityY,
                    0, 0,
                    -canvasHeight, canvasHeight);
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

    public Calendar getRepresentTime() {
        return monthTable.representTime;
    }

    public interface OnDateSelectedListener {
        void onSelect(Calendar c);
    }

    public void setDateSelectedListener(OnDateSelectedListener eventListener) {
        mListener = eventListener;
    }
}
