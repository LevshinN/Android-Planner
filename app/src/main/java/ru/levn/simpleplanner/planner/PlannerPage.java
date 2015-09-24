package ru.levn.simpleplanner.planner;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import ru.levn.simpleplanner.Common;
import ru.levn.simpleplanner.R;
import ru.levn.simpleplanner.calendar.Event;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 24.09.2015.
 */
public class PlannerPage extends Fragment {
    private boolean alignElementsOnTimeScale = false;
    private int scaleMultiplicity = 1;

    static final String ARGUMENT_REPRESENT_DATE = "arg_represent_date";
    long representTime;

    public static PlannerPage newInstance(long date) {
        PlannerPage table = new PlannerPage();
        Bundle arguments = new Bundle();
        arguments.putLong(ARGUMENT_REPRESENT_DATE, date);
        table.setArguments(arguments);
        return table;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        representTime = getArguments().getLong(ARGUMENT_REPRESENT_DATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mRootView = new PlannerView(inflater.getContext());
        mRootView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return mRootView;
    }



}
