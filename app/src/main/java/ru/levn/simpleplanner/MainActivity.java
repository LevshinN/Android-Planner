package ru.levn.simpleplanner;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import ru.levn.simpleplanner.calendar.Calendar;
import ru.levn.simpleplanner.calendar.CalendarDBHelper;
import ru.levn.simpleplanner.calendar.CalendarProvider;
import ru.levn.simpleplanner.fragment.DatePickerFragment;
import ru.levn.simpleplanner.fragment.ScreenCalendars;
import ru.levn.simpleplanner.fragment.ScreenDay;
import ru.levn.simpleplanner.fragment.ScreenMonth;
import ru.levn.simpleplanner.fragment.ScreenWeek;


public class MainActivity extends AppCompatActivity {

    private String[] mScreenTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private ActionBarDrawerToggle mDrawerToggle;

    private Intent mIntent;

    private Toolbar toolbar;
    private View currentMode;

    public static Button btnCurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        Log.d("SCREEN_INFO", "Width = " + dpWidth);
        Log.d("SCREEN_INFO", "Height = " + dpHeight);

        if (savedInstanceState == null) {
            Common.initCurrentDate();
        }

        CalendarProvider.initCalendarProvider(this);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

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


        buildToolbar();

        // Initialize the first fragment when the application first loads.
        if (savedInstanceState == null)  {
            selectItem(Common.DAY_MODE);
        }
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

                StringBuilder message = new StringBuilder();
                message.append(getString(R.string.action_upload) + '\n');
                message.append("Календари:" + '\n');
                for (Calendar cal : CalendarProvider.getEnabledCalendarList()) {
                    message.append(cal.display_name + '\n');
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
            case Common.DAY_MODE:
                fragment = new ScreenDay();
                pressedButton = findViewById(R.id.btn_day_mode);
                break;

            case Common.WEEK_MODE:
                fragment = new ScreenWeek();
                pressedButton = findViewById(R.id.btn_week_mode);
                break;

            case Common.MONTH_MODE:
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

        btnCurrentDate.setText(CalendarProvider.getTextCurrentDate(Common.currentFragment, Common.GetSelectedDate().getTimeInMillis()));
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
                        selectItem(Common.DAY_MODE);
                        break;

                    case R.id.btn_week_mode:
                        selectItem(Common.WEEK_MODE);
                        break;

                    case R.id.btn_month_mode:
                        selectItem(Common.MONTH_MODE);
                        break;

                    case R.id.btn_current_date:
                        showDatePicker();
                        break;

                    default:
                        break;
                }
            }
        };


        btnCurrentDate = (Button)findViewById(R.id.btn_current_date);
        btnCurrentDate.setOnClickListener(listener);
        btnCurrentDate.setText(CalendarProvider.getTextCurrentDate(Common.currentFragment, Common.GetSelectedDate().getTimeInMillis()));


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
        java.util.Calendar selectedDate = Common.GetSelectedDate();
        args.putInt("year", selectedDate.get(java.util.Calendar.YEAR));
        args.putInt("month", selectedDate.get(java.util.Calendar.MONTH));
        args.putInt("day", selectedDate.get(java.util.Calendar.DAY_OF_MONTH));
        date.setArguments(args);

        date.setCallBack(ondate);
        date.show(getFragmentManager(), "Date Picker");
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Common.SetDate(year, monthOfYear, dayOfMonth);
            selectItem(Common.currentFragment);
        }
    };
}
