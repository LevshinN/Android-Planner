package ru.levn.simpleplanner.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;

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
        return new TimePickerDialog(getActivity(), mOnTimeSet, mHour, mMinute, false);
    }
}
