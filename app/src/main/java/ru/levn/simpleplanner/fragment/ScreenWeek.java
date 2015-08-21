package ru.levn.simpleplanner.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;

import com.antonyt.infiniteviewpager.InfinitePagerAdapter;

import java.util.Calendar;

import ru.levn.simpleplanner.Common;
import ru.levn.simpleplanner.R;
import ru.levn.simpleplanner.calendar.CalendarProvider;
import ru.levn.simpleplanner.calendar.Event;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 14.07.2015.
 */

public class ScreenWeek extends ModeFragment {

    private View mRootView;

    public static final int NUM_PAGES = 5;
    private int mCurrentPosition;

    ViewPager mPager;
    PagerAdapter mPagerAdapter;
    InfinitePagerAdapter mInfPagerAdapter;


    PageWeek[] pages = {null, null, null, null, null};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mRootView = inflater.inflate(R.layout.week, container, false);

        mCurrentPosition = 0;

        for (int i = 0; i < 5; ++i) {
            pages[i] = PageWeek.newInstance(mGetNextDate( (i + 2) % 5 - 2 ));
        }

        mPager = (ViewPager) mRootView.findViewById(R.id.pager);
        mPagerAdapter = new MyFragmentPagerAdapter(getActivity().getSupportFragmentManager());
        mInfPagerAdapter = new InfinitePagerAdapter(mPagerAdapter);
        mPager.setAdapter(mInfPagerAdapter);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                int backPosition = (position + NUM_PAGES - 1) % NUM_PAGES;
                int nextPosition = (position + NUM_PAGES + 1) % NUM_PAGES;

                if (mCurrentPosition == backPosition) {
                    CalendarProvider.moveSelectedDate(true);
                    long nextDate = CalendarProvider.getNextPeriod(true);
                    pages[nextPosition] = PageWeek.newInstance(nextDate);

                } else {
                    CalendarProvider.moveSelectedDate(false);
                    long backDate = CalendarProvider.getNextPeriod(false);
                    pages[backPosition] = PageWeek.newInstance(backDate);
                }

                mPagerAdapter.notifyDataSetChanged();
                Common.sUpdateTitle();
                mCurrentPosition = position % NUM_PAGES;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        return mRootView;
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onBuild() {

    }

    private class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return pages[position];
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_UNCHANGED;
        }
    }

    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < 16) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    private long mGetNextDate(int move) {
        Calendar cal = (Calendar)Common.sSelectedDate.getDate().clone();
        cal.add(Calendar.WEEK_OF_YEAR, move);
        return cal.getTimeInMillis();
    }
}

