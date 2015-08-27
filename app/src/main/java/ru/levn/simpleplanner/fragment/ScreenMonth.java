package ru.levn.simpleplanner.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormatSymbols;

import ru.levn.simpleplanner.R;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 14.07.2015.
 */
public class ScreenMonth extends ModeFragment {
    int[] dayNamesProjection = {2,3,4,5,6,7,1};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.month, container, false);
        DateFormatSymbols symbols = DateFormatSymbols.getInstance();
        String[] dayNames = symbols.getShortWeekdays();

        int[] headerIds = {
                R.id.month_1_column,
                R.id.month_2_column,
                R.id.month_3_column,
                R.id.month_4_column,
                R.id.month_5_column,
                R.id.month_6_column,
                R.id.month_7_column
        };

        for (int i = 0; i < 7; ++i) {
            ((TextView)rootView.findViewById(headerIds[i]))
                    .setText(dayNames[dayNamesProjection[i]].toUpperCase());
        }

        return rootView;
    }



    @Override
    public void onUpdate() {
        // TODO
    }

    @Override
    public void onBuild() {
        // TODO
    }
}
