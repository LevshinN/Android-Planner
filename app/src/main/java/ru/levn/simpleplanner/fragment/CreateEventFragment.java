package ru.levn.simpleplanner.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import ru.levn.simpleplanner.Common;
import ru.levn.simpleplanner.R;
import ru.levn.simpleplanner.adapter.CalendarAdapter;
import ru.levn.simpleplanner.adapter.ColorListAdapter;
import ru.levn.simpleplanner.calendar.CalendarProvider;
import ru.levn.simpleplanner.calendar.Event;
import ru.levn.simpleplanner.calendar.MyCalendar;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 04.08.2015.
 */

public class CreateEventFragment extends DialogFragment {

    private static final int EDIT_START_DATE = R.id.edit_event_start_text;
    private static final int EDIT_END_DATE = R.id.edit_event_end_text;


    private static final int DIALOG_DATE = 1;
    private static final int DIALOG_TIME = 2;

    private boolean isEdit;
    private boolean isFirstColorSelect = true;


    private View mRootView;
    private int mDateOnEdit;

    private int mSelectedYear;
    private int mSelectedMonth;
    private int mSelectedDay;
    private int mSelectedHour;
    private int mSelectedMinute;

    private Event mOriginalEvent;
    private Event mNewEvent;

    private OnUpdateEventsInterface mUpdateEvents;

    public interface OnUpdateEventsInterface {
        public void onUpdate();
    }


    static CreateEventFragment newInstance(int num, Event event) {
        CreateEventFragment fragment = new CreateEventFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        fragment.setArguments(args);
        fragment.setEvent(event);

        return fragment;
    }

    private void setEvent(Event e) {
        mOriginalEvent = new Event(e);
        isEdit = true;
    }

    public CreateEventFragment() {
        isEdit = false;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mRootView = inflater.inflate(R.layout.edit_create_event, container, false);

        if (isEdit) mNewEvent = new Event(mOriginalEvent);
        else mNewEvent = new Event();

        Spinner colorSelector = (Spinner)mRootView.findViewById(R.id.edit_event_color);

        int[] colors = getResources().getIntArray(R.array.event_colors_values);
        String[] colorsNames = getResources().getStringArray(R.array.event_colors_names);

        ColorListAdapter colorAdapter = new ColorListAdapter(getActivity(), colors, colorsNames );
        colorSelector.setAdapter(colorAdapter);
        colorSelector.setOnItemSelectedListener(colorSelectorListener);
        colorSelector.setPrompt("Color...");

        CalendarSpinnerAdapter calendarsListAdapter = new CalendarSpinnerAdapter(this.getActivity(),CalendarProvider.calendars);
        Spinner calendarSelector = (Spinner)mRootView.findViewById(R.id.edie_event_calendar);
        calendarSelector.setAdapter(calendarsListAdapter);
        calendarSelector.setOnItemSelectedListener(calendarSelectorListener);

        (mRootView.findViewById(R.id.edit_event_start_text)).setOnClickListener(buttonListener);
        (mRootView.findViewById(R.id.edit_event_end_text)).setOnClickListener(buttonListener);
        (mRootView.findViewById(R.id.edit_event_cancel)).setOnClickListener(buttonListener);
        (mRootView.findViewById(R.id.edit_event_ok)).setOnClickListener(buttonListener);

        (mRootView.findViewById(R.id.edit_event_cancel)).setOnTouchListener(Common.onTouch);
        (mRootView.findViewById(R.id.edit_event_ok)).setOnTouchListener(Common.onTouch);

        if (isEdit) {
            mUpdateDialog();
        }

        return mRootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mUpdateEvents = (OnUpdateEventsInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(Common.sScreenWidth * 4 / 5, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

    private void mUpdateDialog() {
        ((EditText)mRootView.findViewById(R.id.edit_event_title)).setText(mOriginalEvent.title);
        ((EditText)mRootView.findViewById(R.id.edit_event_description))
                .setText(mOriginalEvent.description);
        ((EditText)mRootView.findViewById(R.id.edit_event_location_text))
                .setText(mOriginalEvent.location);

        mRootView.findViewById(R.id.edit_event_title_area).setBackgroundColor( 0xff000000 + mOriginalEvent.color );

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm, dd MMMM yyyy",
                Locale.getDefault());
        ((TextView)mRootView.findViewById(R.id.edit_event_start_text))
                .setText(dateFormat.format(mOriginalEvent.timeStart));
        ((TextView)mRootView.findViewById(R.id.edit_event_end_text))
                .setText(dateFormat.format(mOriginalEvent.timeEnd));

        mRootView.findViewById(R.id.edit_event_calendar_line)
                .setLayoutParams(new LinearLayout.LayoutParams(0,0));
    }

    Spinner.OnItemSelectedListener colorSelectorListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (isFirstColorSelect) {
                isFirstColorSelect = false;
                return;
            }
            mNewEvent.color = (Integer)parent.getSelectedItem();
            mRootView.findViewById(R.id.edit_event_title_area).setBackgroundColor(0xff000000 + mNewEvent.color);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {}
    };

    Spinner.OnItemSelectedListener calendarSelectorListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (!isEdit) mNewEvent.calendarId = ((MyCalendar)parent.getSelectedItem()).id;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    Button.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mDateOnEdit = v.getId();

            switch (mDateOnEdit) {
                case R.id.edit_event_start_text:
                    mShowDatePicker(DIALOG_DATE);
                    break;
                case R.id.edit_event_end_text:
                    mShowDatePicker(DIALOG_DATE);
                    break;
                case R.id.edit_event_ok:
                    mOnFinishEdit();
                    break;
                case R.id.edit_event_cancel:
                    dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    private void mShowDatePicker(int mode) {
        if (mode == DIALOG_DATE) {

            Calendar selectedDate = Common.sSelectedDate.getDate();

            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    mOnDate,
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH)
            );
            dpd.show(getFragmentManager(), "Date Picker");
        }

        else if (mode == DIALOG_TIME) {

            Calendar currentTime = Calendar.getInstance();

            TimePickerDialog dpd = TimePickerDialog.newInstance(
                    mOnTime,
                    currentTime.get(Calendar.HOUR_OF_DAY),
                    currentTime.get(Calendar.MINUTE),
                    true);
            dpd.show(getFragmentManager(), "Time Picker");
        }
    }

    DatePickerDialog.OnDateSetListener mOnDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
            mSelectedYear = year;
            mSelectedMonth = monthOfYear;
            mSelectedDay = dayOfMonth;

            // mEditTime =  "" + dayOfMonth + " " + new DateFormatSymbols().getShortMonths()[monthOfYear % 12] + " " + year;
            mShowDatePicker(DIALOG_TIME);
        }
    };

    TimePickerDialog.OnTimeSetListener mOnTime = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
            mSelectedHour = hourOfDay;
            mSelectedMinute = minute;

            mUpdateStartOrEndTime(mDateOnEdit);
        }
    };

    private void mUpdateStartOrEndTime(int mode) {
        Calendar calendar = new GregorianCalendar(mSelectedYear,
                mSelectedMonth,
                mSelectedDay,
                mSelectedHour,
                mSelectedMinute);

        int editTextID = -1;

        switch (mode) {
            case EDIT_START_DATE:
                mNewEvent.timeStart = calendar.getTimeInMillis();
                editTextID = R.id.edit_event_start_text;
                break;
            case EDIT_END_DATE:
                mNewEvent.timeEnd = calendar.getTimeInMillis();
                editTextID = R.id.edit_event_end_text;
                break;
            default:
                break;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm, dd MMMM yyyy", Locale.getDefault());
        String timeText = dateFormat.format(calendar.getTimeInMillis());
        ((TextView)mRootView.findViewById(editTextID)).setText(timeText);


    }


    private void mOnFinishEdit() {
        EditText titleView = (EditText)mRootView.findViewById(R.id.edit_event_title);
        EditText descriptionView = (EditText)mRootView.findViewById(R.id.edit_event_description);
        EditText locationView = (EditText)mRootView.findViewById(R.id.edit_event_location_text);

        mNewEvent.title = titleView.getText().toString();
        mNewEvent.description = descriptionView.getText().toString();
        mNewEvent.location = locationView.getText().toString();

        if (isEdit) {
            CalendarProvider.editEvent(mOriginalEvent, mNewEvent);
        } else {
            CalendarProvider.saveNewEvent(mNewEvent);
        }

        mUpdateEvents.onUpdate();
        dismiss();

    }
}

class CalendarSpinnerAdapter extends CalendarAdapter {
    private TextView calendarName;

    public CalendarSpinnerAdapter(Context context, ArrayList<MyCalendar> calendars) {
        mCalendarList = calendars;
        mLInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("CalendarsListAdapter", "getView is called");

        View view = convertView;

        if (view == null) {
            LinearLayout linLayout = new LinearLayout(mLInflater.getContext());
            linLayout.setLayoutParams( new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT) );
            ViewGroup.LayoutParams lpView = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            calendarName = new TextView(mLInflater.getContext());
            calendarName.setLayoutParams(lpView);
            calendarName.setTextAppearance(mLInflater.getContext(), android.R.style.TextAppearance_DeviceDefault_Medium);
            calendarName.setPadding(10,10,10,10);
            linLayout.addView(calendarName);

            view = linLayout;
        }


        MyCalendar cal = (MyCalendar)getItem(position);
        calendarName.setText(cal.displayName);

        return view;
    }
}
