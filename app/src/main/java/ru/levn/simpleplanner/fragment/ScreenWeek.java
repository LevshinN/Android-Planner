package ru.levn.simpleplanner.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.levn.simpleplanner.R;

/**
 * Created by Levshin_N on 14.07.2015.
 */
public class ScreenWeek extends Fragment {

    public ScreenWeek() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.week, container,
                false);

        return rootView;
    }
}
