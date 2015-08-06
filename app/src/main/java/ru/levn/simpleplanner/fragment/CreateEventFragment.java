package ru.levn.simpleplanner.fragment;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import ru.levn.simpleplanner.Common;
import ru.levn.simpleplanner.R;
import ru.levn.simpleplanner.adapter.ColorListAdapter;

/**
 * Created by Levshin_N on 04.08.2015.
 */
public class CreateEventFragment extends DialogFragment {

    private static final int EDIT_START_DATE = R.id.edit_event_start_button;
    private static final int EDIT_END_DATE = R.id.edit_event_end_button;


    private static final int DIALOG_DATE = 1;
    private static final int DIALOG_TIME = 2;


    private View rootView;
    private int dateOnEdit;

    private String editTime;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        rootView = inflater.inflate(R.layout.edit_create_event, container, false);

        Spinner colorSelector = (Spinner)rootView.findViewById(R.id.edit_event_color);

        int[] colors = getResources().getIntArray(R.array.event_colors_values);
        String[] colorsNames = getResources().getStringArray(R.array.event_colors_names);

        ColorListAdapter colorAdapter = new ColorListAdapter(getActivity(), colors, colorsNames );
        colorSelector.setAdapter(colorAdapter);
        colorSelector.setOnItemSelectedListener(colorSelectorListener);

        (rootView.findViewById(R.id.edit_event_start_button)).setOnClickListener(buttonListener);
        (rootView.findViewById(R.id.edit_event_end_button)).setOnClickListener(buttonListener);

        return rootView;
    }

    Spinner.OnItemSelectedListener colorSelectorListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            int selectedColor = (Integer)parent.getSelectedItem();
            rootView.findViewById(R.id.edit_event_title_area).setBackgroundColor(selectedColor);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {}
    };

    Button.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dateOnEdit = v.getId();
            showDatePicker(DIALOG_DATE);
        }
    };

    private void showDatePicker(int mode) {
        if (mode == DIALOG_DATE) {

            DatePickerFragment date = new DatePickerFragment();

            Bundle args = new Bundle();
            Calendar selectedDate = Common.GetSelectedDate();
            args.putInt("year", selectedDate.get(Calendar.YEAR));
            args.putInt("month", selectedDate.get(Calendar.MONTH));
            args.putInt("day", selectedDate.get(Calendar.DAY_OF_MONTH));
            date.setArguments(args);
            date.setCallBack(ondate);
            date.show(getFragmentManager(), "Date Picker");
        }

        else if (mode == DIALOG_TIME) {
            TimePickerFragment time = new TimePickerFragment();

            Bundle args = new Bundle();
            Calendar currentTime = Calendar.getInstance();
            args.putInt("hour", currentTime.get(Calendar.HOUR_OF_DAY));
            args.putInt("minute", currentTime.get(Calendar.MINUTE));

            time.setArguments(args);
            time.setCallBack(ontime);
            time.show(getFragmentManager(), "Time Picker");
        }
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            editTime =  "" + dayOfMonth + " " + new DateFormatSymbols().getShortMonths()[monthOfYear % 12] + " " + year;
            showDatePicker(DIALOG_TIME);
        }
    };

    TimePickerDialog.OnTimeSetListener ontime = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            editTime = "" + hourOfDay + ":" + minute + " " + editTime;

            int editTextID = -1;
            switch(dateOnEdit) {
                case EDIT_START_DATE:
                    editTextID = R.id.edit_event_start_text;
                    break;
                case EDIT_END_DATE:
                    editTextID = R.id.edit_event_end_text;
                    break;
                default:
                    System.err.print("Invalid parameter for edit date mode!");
                    break;
            }

            ((TextView)rootView.findViewById(editTextID)).setText(editTime);
        }
    };
}