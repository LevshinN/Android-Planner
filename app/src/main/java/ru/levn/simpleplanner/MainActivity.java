package ru.levn.simpleplanner;


import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import ru.levn.simpleplanner.calendar.CalendarProvider;
import ru.levn.simpleplanner.calendar.syncadapter.SyncUtils;
import ru.levn.simpleplanner.fragment.CreateEventFragment;
import ru.levn.simpleplanner.fragment.ModeFragment;
import ru.levn.simpleplanner.fragment.ScreenCalendars;
import ru.levn.simpleplanner.fragment.ScreenDay;
import ru.levn.simpleplanner.fragment.ScreenMonth;
import ru.levn.simpleplanner.fragment.ScreenWeek;
import ru.levn.simpleplanner.planner.Planner;


public class MainActivity extends AppCompatActivity
        implements Common.OnUpdateEventsInterface{

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private static ModeFragment mCurrentFragment;
    private static ModeFragment mSupportFragment;

    private View mCurrentMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SyncUtils.CreateSyncAccount(this);

        Common.sIsDrawerClosed = true;

        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        Common.sScreenWidth = displayMetrics.widthPixels;
        Common.sScreenHeight = displayMetrics.heightPixels;
        Common.sScreenDensity = displayMetrics.density;

        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        Log.d("SCREEN_INFO", "Width = " + dpWidth);
        Log.d("SCREEN_INFO", "Height = " + dpHeight);

        CalendarProvider.sInitCalendarProvider(this);

        if (savedInstanceState == null) {
            Common.init();
            Common.sMainActivity = this;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);


        String[] mScreenTitles = getResources().getStringArray(R.array.screen_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);



        mDrawerToggle = new ActionBarDrawerToggle(
                this, /* host Activity */
                mDrawerLayout, /* DrawerLayout object */
                R.string.drawer_open, /* "open drawer" description */
                R.string.drawer_close /* "close drawer" description */
                ) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                Common.sIsDrawerClosed = true;
                mCurrentFragment.onUpdate();
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View view) {
                Common.sIsDrawerClosed = false;
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, mScreenTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());


        mBuildToolbar();

        // Добавляем  дополнительный фрагмент
        if (findViewById(R.id.content_frame_main) != null) {
            mSupportFragment = new ScreenMonth();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame_main, mSupportFragment, "main")
                    .commit();
        }

        // Initialize the first fragment when the application first loads.
        if (savedInstanceState == null)  {
            mSelectItem(Common.DAY_MODE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUpdate() {
        mSelectItem(Common.sCurrentMode);
    }


    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSelectItem(position);
        }
    }

    /** Swaps fragments in the main content view */
    private void mSelectItem(int position) {

        // Update the main content by replacing fragments
        mCurrentFragment = null;
        View pressedButton = null;

        final int[] projection = getResources().getIntArray(R.array.modes_projection);

        switch (projection[position]) {
            case Common.DAY_MODE:
                mCurrentFragment = new Planner();
                Common.sBtnCurrentDate.setVisibility(View.VISIBLE);
                pressedButton = findViewById(R.id.btn_day_mode);
                break;

            case Common.WEEK_MODE:
                mCurrentFragment = new ScreenWeek();
                Common.sBtnCurrentDate.setVisibility(View.VISIBLE);
                pressedButton = findViewById(R.id.btn_week_mode);
                break;

            case Common.MONTH_MODE:
                mCurrentFragment = new ScreenMonth();
                mCurrentFragment.setLastMainMode(Common.sCurrentMode);
                Common.sBtnCurrentDate.setVisibility(View.VISIBLE);
                pressedButton = findViewById(R.id.btn_month_mode);
                break;

            case 3:
                mCurrentFragment = new ScreenCalendars();
                Common.sBtnCurrentDate.setVisibility(View.INVISIBLE);

                // Обновляем список календарей
                CalendarProvider.sUpdateCalendars();

                if (mCurrentMode != null) {
                    mCurrentMode.setPressed(false);
                }
                break;

            default:
                break;
        }

        // Insert the fragment by replacing any existing fragment
        if (mCurrentFragment != null) {

            FragmentManager fragmentManager = this.getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.content_frame_support,
                    mCurrentFragment,
                    "support");
            transaction.addToBackStack("support");
            transaction.commit();

            if (mSupportFragment != null) {
                mSupportFragment.setLastMainMode(projection[position]);
            }

            // Highlight the selected item, update the title, and close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerLayout.closeDrawer(mDrawerList);

        } else {
            // Error
            Log.e(this.getClass().getName(), "Error. Fragment is not created");
        }

        if (pressedButton != null) {

            if (mCurrentMode != null) {
                mCurrentMode.setSelected(false);
            }

            pressedButton.setSelected(true);

            mCurrentMode = pressedButton;
        }

        Common.sCurrentMode = position;
        Common.sUpdateTitle(Common.sSelectedDate.getDate());
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
        mSelectItem(Common.sCurrentMode);
    }

    private void mBuildToolbar() {

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.btn_day_mode:
                        mSelectItem(Common.DAY_MODE);
                        break;

                    case R.id.btn_week_mode:
                        mSelectItem(Common.WEEK_MODE);
                        break;

                    case R.id.btn_month_mode:
                        mSelectItem(Common.MONTH_MODE);
                        break;

                    case R.id.btn_current_date:
                        mShowDatePicker();
                        break;

                    case R.id.btn_add:
                        DialogFragment editEventDialog = new CreateEventFragment();
                        editEventDialog.show(getFragmentManager(), "create_new_event");
                        break;

                    case R.id.btn_today:
                        Common.sSelectedDate.setDate(Calendar.getInstance());
                        onUpdate();
                        break;

                    default:
                        break;
                }
            }
        };


        Common.sBtnCurrentDate = findViewById(R.id.btn_current_date);
        Common.sBtnCurrentDate.setOnClickListener(listener);
        Common.sUpdateTitle(Common.sSelectedDate.getDate());

        findViewById(R.id.btn_add).setOnClickListener(listener);
        findViewById(R.id.btn_today).setOnClickListener(listener);

        View btnDay = findViewById(R.id.btn_day_mode);
        View btnWeek = findViewById(R.id.btn_week_mode);
        View btnMonth = findViewById(R.id.btn_month_mode);

        if (btnDay != null) btnDay.setOnClickListener(listener);
        if (btnWeek != null) btnWeek.setOnClickListener(listener);
        if (btnMonth != null) btnMonth.setOnClickListener(listener);
    }

    private void mShowDatePicker() {

        Calendar selectedDate = Common.sSelectedDate.getDate();

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                mOnDate,
                selectedDate.get(java.util.Calendar.YEAR),
                selectedDate.get(java.util.Calendar.MONTH),
                selectedDate.get(java.util.Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Date Picker");
    }

    DatePickerDialog.OnDateSetListener mOnDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
            Common.sSelectedDate.setDate(year, monthOfYear, dayOfMonth);
            mSelectItem(Common.sCurrentMode);
        }
    };

    @Override
    public void onBackPressed() {
        int fragments = getSupportFragmentManager().getBackStackEntryCount();
        if (fragments == 1) {
            finish();
        }

        super.onBackPressed();

        mDrawerList.setItemChecked(Common.sCurrentMode, true);
    }

    @Override
    public void onPostResume() {
        super.onPostResume();
        Common.sMainActivity = this;
    }

}
