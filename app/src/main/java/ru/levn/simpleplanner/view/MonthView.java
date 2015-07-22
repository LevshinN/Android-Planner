package ru.levn.simpleplanner.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Levshin_N on 22.07.2015.
 */
public class MonthView extends View {
    // Конструктор, необходимый для создания элемента внутри кода программы
    public MonthView(Context context) {
        super(context);
    }

    // Конструктор, необходимый для наполнения элемента из файла с ресурсом
    public MonthView (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // Конструктор, необходимый для наполнения элемента из файла с ресурсом
    public MonthView (Context context, AttributeSet ats, int defaultStyle) {
        super(context, ats, defaultStyle );
    }

    @Override
    protected void onMeasure(int wMeasureSpec, int hMeasureSpec) {
        int measuredHeight = measureHeight(hMeasureSpec);
        int measuredWidth = measureWidth(wMeasureSpec);
        // Вы ДОЛЖНЫ сделать вызов метода setMeasuredDimension,
        // иначе получится выброс исключения при
        // размещении элемента внутри разметки.
        setMeasuredDimension(measuredHeight, measuredWidth);
    }

    private int measureHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        //[ ... Вычисление высоты Представления ... ]
        return specSize;
    }

    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        //[ ... Вычисление ширины Представления  ... ]
        return specSize;
    }
}
