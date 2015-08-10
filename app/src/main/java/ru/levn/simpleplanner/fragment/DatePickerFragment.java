package ru.levn.simpleplanner.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 21.07.2015.
 */

public class DatePickerFragment extends DialogFragment {
    DatePickerDialog.OnDateSetListener mOnDateSet;

    public DatePickerFragment() {
    }

    public void setCallBack(DatePickerDialog.OnDateSetListener ondate) {
        mOnDateSet = ondate;
    }

    private int mYear, mMonth, mDay;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        mYear = args.getInt("year");
        mMonth = args.getInt("month");
        mDay = args.getInt("day");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), mOnDateSet, mYear, mMonth, mDay);
    }
}
