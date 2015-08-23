package ru.levn.simpleplanner.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.levn.simpleplanner.Common;
import ru.levn.simpleplanner.calendar.Event;

/**
 * Created by Levshin_N on 22.07.2015.
 */
public class MonthView extends View {

    private boolean measurementChanged = true;

    private MonthTable monthTable;

    // Конструктор, необходимый для создания элемента внутри кода программы
    public MonthView(Context context) {
        super(context);
        monthTable = new MonthTable(context, Common.sSelectedDate.getDate());
    }

    // Конструктор, необходимый для наполнения элемента из файла с ресурсом
    public MonthView (Context context, AttributeSet attrs) {
        super(context, attrs);
        monthTable = new MonthTable(context, Common.sSelectedDate.getDate());
    }

    // Конструктор, необходимый для наполнения элемента из файла с ресурсом
    public MonthView (Context context, AttributeSet ats, int defaultStyle) {
        super(context, ats, defaultStyle );
        monthTable = new MonthTable(context, Common.sSelectedDate.getDate());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (measurementChanged) {
            measurementChanged = false;
            monthTable.initializeTable(getMeasuredWidth(), getMeasuredHeight());
        }

        monthTable.draw(canvas);
    }




}
