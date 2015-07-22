package ru.levn.simpleplanner.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import ru.levn.simpleplanner.R;

/**
 * Created by Levshin_N on 14.07.2015.
 */
public class ScreenMonth extends Fragment {

    public ScreenMonth() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.month, container, false);



        return rootView;
    }
}
