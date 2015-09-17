package ru.levn.simpleplanner.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import ru.levn.simpleplanner.Common;
import ru.levn.simpleplanner.R;
import ru.levn.simpleplanner.adapter.CalendarAdapter;
import ru.levn.simpleplanner.adapter.ColorListAdapter;
import ru.levn.simpleplanner.calendar.CalendarProvider;
import ru.levn.simpleplanner.calendar.Event;
import ru.levn.simpleplanner.calendar.MyCalendar;
import ru.levn.simpleplanner.calendar.RRule;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 04.08.2015.
 */

public class CreateEventFragment extends DialogFragment
        implements CompoundButton.OnCheckedChangeListener, View.OnClickListener{

    private static final int EDIT_START_DATE = R.id.edit_event_start_text;
    private static final int EDIT_END_DATE = R.id.edit_event_end_text;


    private static final int DIALOG_DATE = 1;
    private static final int DIALOG_TIME = 2;

    private boolean isEdit;
    private boolean isFirstColorSelect = true;
    private boolean isRepeatModeSelectorClosed = true;
    private boolean isUntilSetting = false;


    private View mRootView;
    private int mButtonId;

    private int mSelectedYear;
    private int mSelectedMonth;
    private int mSelectedDay;
    private int mSelectedHour;
    private int mSelectedMinute;

    private RRule mRecRule;
    private Calendar mUntil;
    private RRule.EEndMode mRecRuleEndMode = RRule.EEndMode.EM_FOREVER;

    private Event mOriginalEvent;
    private Event mNewEvent;

    private Common.OnUpdateEventsInterface mUpdateEvents;

    private View mModeSelector;

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
        mRootView.findViewById(R.id.edit_event_title_area).setBackgroundResource(R.color.default_color);

        if (isEdit) mNewEvent = new Event(mOriginalEvent);
        else {
            mNewEvent = new Event();
            mNewEvent.timeStart = Common.sSelectedDate.getDate().getTimeInMillis();
            Calendar c = (Calendar)Common.sSelectedDate.getDate().clone();
            c.add(Calendar.HOUR_OF_DAY, 1);
            mNewEvent.timeEnd = c.getTimeInMillis();
        }


        // Спиннер для выбора цвета события
        Spinner colorSelector = (Spinner)mRootView.findViewById(R.id.edit_event_color);
        int[] colors = getResources().getIntArray(R.array.event_colors_values);
        String[] colorsNames = getResources().getStringArray(R.array.event_colors_names);
        ColorListAdapter colorAdapter = new ColorListAdapter(getActivity(), colors, colorsNames );
        colorSelector.setAdapter(colorAdapter);
        colorSelector.setOnItemSelectedListener(colorSelectorListener);


        // Спиннер для выбора календаря
        CalendarSpinnerAdapter calendarsListAdapter =
                new CalendarSpinnerAdapter(this.getActivity(),CalendarProvider.calendars);
        Spinner calendarSelector = (Spinner)mRootView.findViewById(R.id.edie_event_calendar);
        calendarSelector.setAdapter(calendarsListAdapter);
        calendarSelector.setOnItemSelectedListener(calendarSelectorListener);

        // Спиннер для выбора, как ограничивать последовательность событий
        String[] data = getResources().getStringArray(R.array.repeat_border);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this.getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                data);
        Spinner borderMode = (Spinner)mRootView.findViewById(R.id.edit_event_border_mode);
        borderMode.setAdapter(adapter);
        borderMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            View button = mRootView.findViewById(R.id.edit_event_repeat_until);
            View editText = mRootView.findViewById(R.id.edit_event_repeat_count);

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mRecRuleEndMode = RRule.EEndMode.EM_COUNT;
                        editText.setVisibility(View.VISIBLE);
                        button.setVisibility(View.GONE);
                        break;
                    case 1:
                        mRecRuleEndMode = RRule.EEndMode.EM_UNTIL;
                        editText.setVisibility(View.GONE);
                        button.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });



        // Здесь на все кнопки меню вешается слушатель события нажатия.
        mRootView.findViewById(R.id.edit_event_repeat_mode_button).setOnClickListener(this);
        mRootView.findViewById(R.id.edit_event_start_text).setOnClickListener(this);
        mRootView.findViewById(R.id.edit_event_end_text).setOnClickListener(this);
        mRootView.findViewById(R.id.edit_event_cancel).setOnClickListener(this);
        mRootView.findViewById(R.id.edit_event_ok).setOnClickListener(this);
        mRootView.findViewById(R.id.edit_event_repeat_mode_year).setOnClickListener(this);
        mRootView.findViewById(R.id.edit_event_repeat_mode_month).setOnClickListener(this);
        mRootView.findViewById(R.id.edit_event_repeat_mode_week).setOnClickListener(this);
        mRootView.findViewById(R.id.edit_event_repeat_mode_day).setOnClickListener(this);
        mRootView.findViewById(R.id.edit_event_repeat_delete).setOnClickListener(this);
        mRootView.findViewById(R.id.edit_event_repeat_advanced_button).setOnClickListener(this);
        mRootView.findViewById(R.id.edit_event_repeat_until).setOnClickListener(this);

        // Свитчер на установку события на весь день
        Switch s = (Switch) mRootView.findViewById(R.id.edit_event_all_day_switcher);
        if (s != null) {
            s.setOnCheckedChangeListener(this);
        }

        mModeSelector = mRootView.findViewById(R.id.edit_event_repeat_mode_selector);
        isRepeatModeSelectorClosed = true;

        mUpdateDialog();

        return mRootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mUpdateEvents = (Common.OnUpdateEventsInterface) activity;
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
        window.setLayout(Common.sScreenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

    private void mUpdateDialog() {
        if (isEdit) {
            ((EditText)mRootView.findViewById(R.id.edit_event_title)).setText(mOriginalEvent.title);
            ((EditText)mRootView.findViewById(R.id.edit_event_description))
                    .setText(mOriginalEvent.description);
            ((EditText)mRootView.findViewById(R.id.edit_event_location_text))
                    .setText(mOriginalEvent.location);

            mRootView.findViewById(R.id.edit_event_title_area).setBackgroundColor(0xff000000 + mOriginalEvent.color);

            mRootView.findViewById(R.id.edit_event_calendar_line)
                    .setLayoutParams(new LinearLayout.LayoutParams(0, 0));

            ((Switch)mRootView.findViewById(R.id.edit_event_all_day_switcher)).setChecked(mNewEvent.isAllDay);
        }

        mUpdateTimeTexts();
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

            if (isUntilSetting) {
                mUntil = Calendar.getInstance();
                mUntil.set(year, monthOfYear, dayOfMonth);
                ((Button)mRootView.findViewById(R.id.edit_event_repeat_until))
                        .setText(CalendarProvider.getDate(mUntil.getTimeInMillis()));
                return;
            }

            if (mNewEvent.isAllDay) {
                mUpdateStartOrEndTime(mButtonId);
            } else {
                mShowDatePicker(DIALOG_TIME);
            }
        }
    };

    TimePickerDialog.OnTimeSetListener mOnTime = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
            mSelectedHour = hourOfDay;
            mSelectedMinute = minute;

            mUpdateStartOrEndTime(mButtonId);
        }
    };

    private void mUpdateStartOrEndTime(int mode) {
        Calendar calendar = new GregorianCalendar(mSelectedYear,
                mSelectedMonth,
                mSelectedDay,
                mSelectedHour,
                mSelectedMinute);

        if (mNewEvent.isAllDay) {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            if (mode == EDIT_END_DATE) {
                calendar.getTimeInMillis();
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                calendar.getTimeInMillis();
            }
        }

        switch (mode) {
            case EDIT_START_DATE:
                mNewEvent.timeStart = calendar.getTimeInMillis();

                if (mNewEvent.timeStart >= mNewEvent.timeEnd) {
                    calendar.add(Calendar.HOUR_OF_DAY, 1);
                    mNewEvent.timeEnd = calendar.getTimeInMillis();
                }
                break;
            case EDIT_END_DATE:
                mNewEvent.timeEnd = calendar.getTimeInMillis();

                if (mNewEvent.timeEnd <= mNewEvent.timeStart) {
                    calendar.add(Calendar.HOUR_OF_DAY, -1);
                    mNewEvent.timeStart = calendar.getTimeInMillis();
                }
                break;
            default:
                break;
        }

        mUpdateTimeTexts();
    }

    private void mUpdateTimeTexts() {
        SimpleDateFormat dateFormat;

        if (mNewEvent.isAllDay) {
            dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        } else {
            dateFormat = new SimpleDateFormat("HH:mm, dd MMMM yyyy", Locale.getDefault());
        }

        TextView start = (TextView) mRootView.findViewById(R.id.edit_event_start_text);
        TextView end = (TextView) mRootView.findViewById(R.id.edit_event_end_text);
        start.setText(dateFormat.format(mNewEvent.timeStart));
        end.setText(dateFormat.format(mNewEvent.timeEnd));
    }


    private void mOnFinishEdit() {
        EditText titleView = (EditText)mRootView.findViewById(R.id.edit_event_title);
        EditText descriptionView = (EditText)mRootView.findViewById(R.id.edit_event_description);
        EditText locationView = (EditText)mRootView.findViewById(R.id.edit_event_location_text);
        EditText intervalView = (EditText)mRootView.findViewById(R.id.edit_event_repeat_interval);

        mNewEvent.title = titleView.getText().toString();
        mNewEvent.description = descriptionView.getText().toString();
        mNewEvent.location = locationView.getText().toString();

        if (mNewEvent.title.equals("")) {
            Toast.makeText(getActivity(), R.string.msg_no_empty_title, Toast.LENGTH_SHORT).show();
            return;
        }

        if (mRecRule != null) {
            String intervalRule = intervalView.getText().toString();
            if (!intervalRule.equals(""))  mRecRule.setInterval(intervalRule);

            switch (mRecRuleEndMode) {
                case EM_COUNT:
                    EditText countView = (EditText)mRootView.findViewById(R.id.edit_event_repeat_count);
                    String countRule = countView.getText().toString();
                    if (!countRule.equals(""))  mRecRule.setCount(countRule);
                    break;
                case EM_UNTIL:
                    if (mUntil != null) {
                        Calendar c = (Calendar)mUntil.clone();
                        c.setTimeInMillis(mNewEvent.timeEnd);
                        c.getTimeInMillis();
                        c.set(Calendar.YEAR, mUntil.get(Calendar.YEAR));
                        c.set(Calendar.MONTH, mUntil.get(Calendar.MONTH));
                        c.set(Calendar.DAY_OF_MONTH, mUntil.get(Calendar.DAY_OF_MONTH));
                        c.getTimeInMillis();
                        c.setTimeZone(TimeZone.getTimeZone("UTC"));

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
                        String untilRule = simpleDateFormat.format(new Date(c.getTimeInMillis()));
                        mRecRule.setUntil(untilRule);
                    }
                    break;
                default:
                    break;
            }
            mNewEvent.rrule = mRecRule.getRule();
        }

        if (isEdit) {
            CalendarProvider.editEvent(mOriginalEvent, mNewEvent);
        } else {
            CalendarProvider.saveNewEvent(mNewEvent);
        }

        Common.sEvents.update();
        mUpdateEvents.onUpdate();
        dismiss();

    }

    private void mOnEditRepeatModeSelector() {
        if (isRepeatModeSelectorClosed) {
            mModeSelector.setVisibility(View.VISIBLE);
        } else {
            mModeSelector.setVisibility(View.GONE);
        }
        isRepeatModeSelectorClosed = !isRepeatModeSelectorClosed;
    }

    private void mOnCreateRRule(int modeId) {
        mRecRule = new RRule();
        String freqRule;
        String buttonTextEnding;
        switch (modeId) {
            case R.id.edit_event_repeat_mode_year:
                freqRule = "YEARLY";
                buttonTextEnding = getResources().getString(R.string.yearly).toLowerCase();
                break;
            case R.id.edit_event_repeat_mode_month:
                freqRule = "MONTHLY";
                buttonTextEnding = getResources().getString(R.string.monthly).toLowerCase();
                break;
            case R.id.edit_event_repeat_mode_week:
                freqRule = "WEEKLY";
                buttonTextEnding = getResources().getString(R.string.weekly).toLowerCase();
                break;
            case R.id.edit_event_repeat_mode_day:
                freqRule = "DAILY";
                buttonTextEnding = getResources().getString(R.string.daily).toLowerCase();
                break;
            default:
                mRecRule = null;
                return;
        }
        mRecRule.setFreq(freqRule);
        Button b = (Button)mRootView.findViewById(R.id.edit_event_repeat_mode_button);
        b.setText(getResources().getString(R.string.repeat) + " " + buttonTextEnding + ".");

        ImageButton c = (ImageButton)mRootView.findViewById(R.id.edit_event_repeat_delete);
        c.setVisibility(View.VISIBLE);

        mOnEditRepeatModeSelector();

        b = (Button)mRootView.findViewById(R.id.edit_event_repeat_advanced_button);
        b.setVisibility(View.VISIBLE);
    }

    private void mOnDeleteRRule() {
        mRecRule = null;

        Button b = (Button)mRootView.findViewById(R.id.edit_event_repeat_mode_button);
        b.setText(getResources().getString(R.string.repeat_ellipsis));

        View v = mRootView.findViewById(R.id.edit_event_repeat_delete);
        v.setVisibility(View.INVISIBLE);

        if (!isRepeatModeSelectorClosed) {
            mOnEditRepeatModeSelector();
        }

        v = mRootView.findViewById(R.id.edit_event_repeat_advanced_button);
        v.setVisibility(View.GONE);

        v = mRootView.findViewById(R.id.edit_event_repeat_advanced);
        TextView interval = (TextView)v.findViewById(R.id.edit_event_repeat_interval);
        interval.clearComposingText();

        TextView count = (TextView)v.findViewById(R.id.edit_event_repeat_count);
        count.clearComposingText();

        b = (Button)v.findViewById(R.id.edit_event_repeat_until);
        b.setText(R.string.finish_date);
        mUntil = null;

        v.setVisibility(View.GONE);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();

        switch(id) {
            case R.id.edit_event_all_day_switcher:
                mNewEvent.isAllDay = isChecked;
                mUpdateTimeTexts();
        }
    }

    public void mOnOpenAdvanceSettings() {
        View v = mRootView.findViewById(R.id.edit_event_repeat_advanced);
        if (v.getVisibility() == View.VISIBLE) {
            v.setVisibility(View.GONE);
        } else {
            v.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        mButtonId = v.getId();

        switch (mButtonId) {
            case R.id.edit_event_repeat_mode_button:
                mOnEditRepeatModeSelector();
                break;
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
            case R.id.edit_event_repeat_mode_year:
            case R.id.edit_event_repeat_mode_month:
            case R.id.edit_event_repeat_mode_week:
            case R.id.edit_event_repeat_mode_day:
                mOnCreateRRule(mButtonId);
                break;
            case R.id.edit_event_repeat_delete:
                mOnDeleteRRule();
                break;
            case R.id.edit_event_repeat_advanced_button:
                mOnOpenAdvanceSettings();
                break;
            case R.id.edit_event_repeat_until:
                isUntilSetting = true;
                mShowDatePicker(DIALOG_DATE);
                break;
            default:
                break;
        }
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
