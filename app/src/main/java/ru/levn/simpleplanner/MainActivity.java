package ru.levn.simpleplanner;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import ru.levn.simpleplanner.adapter.CalendarsListAdapter;
import ru.levn.simpleplanner.calendar.CalendarProvider;
import ru.levn.simpleplanner.fragment.DatePickerFragment;
import ru.levn.simpleplanner.fragment.ScreenCalendars;
import ru.levn.simpleplanner.fragment.ScreenDay;
import ru.levn.simpleplanner.fragment.ScreenMonth;
import ru.levn.simpleplanner.fragment.ScreenWeek;


public class MainActivity extends AppCompatActivity {

    private static final int DAY_MODE = 0;
    private static final int WEEK_MODE = 1;
    private static final int MONTH_MODE = 2;

    private String[] mScreenTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private ActionBarDrawerToggle mDrawerToggle;

    private CalendarProvider mCalendarProvider;

    private Intent mIntent;

    private Toolbar toolbar;
    private View currentMode;

    private Button btnCurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Common.initCurrentDate();

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        mCalendarProvider = new CalendarProvider(this);
        mIntent = getIntent();

        mScreenTitles = getResources().getStringArray(R.array.screen_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);



        mDrawerToggle = new ActionBarDrawerToggle(
                this, /* host Activity */
                mDrawerLayout, /* DrawerLayout object */
                R.string.drawer_open, /* "open drawer" description */
                R.string.drawer_close /* "close drawer" description */
                ) {
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mScreenTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Initialize the first fragment when the application first loads.
        if (savedInstanceState == null)  {
            selectItem(DAY_MODE);
        }

        buildToolbar();
    }

    @Override
    public void onResume() {
        super.onResume();
        selectItem(Common.currentFragment);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle action buttons
        switch(item.getItemId()) {
            case R.id.action_add:
                // Show toast about click.
                Toast.makeText(this, R.string.action_add, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_upload:
                // Show toast about click.
                Map<String,String> calendars = mCalendarProvider.GetCalendars();

                StringBuilder message = new StringBuilder();
                message.append(getString(R.string.action_upload) + '\n');
                message.append("Календари:" + '\n');
                for (Map.Entry<String,String> val : calendars.entrySet()) {
                    if (Common.selectedCalendarsIDs.get(val.getKey())) {
                        message.append(val.getValue() + '\n');
                    }
                }

                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        // Update the main content by replacing fragments
        Common.currentFragment = position;
        Fragment fragment = null;
        View pressedButton = null;
        switch (position) {
            case DAY_MODE:
                fragment = new ScreenDay();
                pressedButton = findViewById(R.id.btn_day_mode);
                break;

            case WEEK_MODE:
                fragment = new ScreenWeek();
                pressedButton = findViewById(R.id.btn_week_mode);
                break;

            case MONTH_MODE:
                fragment = new ScreenMonth();
                pressedButton = findViewById(R.id.btn_month_mode);
                break;

            case 3:
                fragment = new ScreenCalendars();

                // Обновляем список календарей
                CalendarProvider.updateCalendars(this);

                if (currentMode != null) {
                    currentMode.setBackgroundResource(android.R.color.transparent);
                }

                Log.d("MainActivity", "-----------OK----------" );
                break;

            default:
                break;
        }

        // Insert the fragment by replacing any existing fragment
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            // Highlight the selected item, update the title, and close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // Error
            Log.e(this.getClass().getName(), "Error. Fragment is not created");
        }

        if (pressedButton != null) {

            if (currentMode != null) {
                currentMode.setBackgroundResource(android.R.color.transparent);
            }

            pressedButton.setBackgroundResource(R.color.dark_yellow);

            currentMode = pressedButton;
        }
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
        selectItem(Common.currentFragment);
    }

    private void buildToolbar() {

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.btn_day_mode:
                        selectItem(DAY_MODE);
                        return;
                    case R.id.btn_week_mode:
                        selectItem(WEEK_MODE);
                        return;
                    case R.id.btn_month_mode:
                        selectItem(MONTH_MODE);
                        return;
                    case R.id.btn_current_date:
                        showDatePicker();

                }
            }
        };


        btnCurrentDate = (Button)findViewById(R.id.btn_current_date);
        btnCurrentDate.setOnClickListener(listener);
        btnCurrentDate.setText(Common.getTextCurrentDate());


        Button btnDay = (Button)findViewById(R.id.btn_day_mode);
        Button btnWeek = (Button)findViewById(R.id.btn_week_mode);
        Button btnMonth = (Button)findViewById(R.id.btn_month_mode);

        if (btnDay != null && btnWeek != null && btnMonth != null) {
            btnDay.setOnClickListener(listener);
            btnWeek.setOnClickListener(listener);
            btnMonth.setOnClickListener(listener);
        }
    }

    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();

        Bundle args = new Bundle();
        args.putInt("year", Common.year);
        args.putInt("month", Common.month);
        args.putInt("day", Common.day);
        date.setArguments(args);

        date.setCallBack(ondate);
        date.show(getFragmentManager(), "Date Picker");
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Common.year = year;
            Common.month = monthOfYear;
            Common.day = dayOfMonth;

            selectItem(Common.currentFragment);
            btnCurrentDate.setText(Common.getTextCurrentDate());
        }
    };


}
