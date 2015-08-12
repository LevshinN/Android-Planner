package ru.levn.simpleplanner.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TimePicker;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 21.07.2015.
 */

public class DatePickerFragment extends DialogFragment {
    DatePickerDialog.OnDateSetListener mOnDateSet;
    DatePickerDialog mDatePicker;

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
        mDatePicker = new DatePickerDialog(getActivity(), mOnDateSet, mYear, mMonth, mDay);
        return mDatePicker;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        //to hide keyboard when showing dialog fragment
        View view = super.onCreateView(inflater, container, savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mDatePicker.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);

        return view;
    }

}
