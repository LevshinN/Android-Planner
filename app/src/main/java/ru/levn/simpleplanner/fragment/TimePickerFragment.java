package ru.levn.simpleplanner.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 06.08.2015.
 */

public class TimePickerFragment extends DialogFragment {

    TimePickerDialog.OnTimeSetListener mOnTimeSet;

    public void setCallBack(TimePickerDialog.OnTimeSetListener onTime) {
        mOnTimeSet = onTime;
    }

    private int mHour, mMinute;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        mHour = args.getInt("hour");
        mMinute = args.getInt("minute");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(getActivity(), mOnTimeSet, mHour, mMinute, true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
