package ru.levn.simpleplanner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import ru.levn.simpleplanner.calendar.MyCalendar;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 10.08.2015.
 */
public class CalendarAdapter extends BaseAdapter {
    protected LayoutInflater mLInflater;
    protected ArrayList<MyCalendar> mCalendarList;

    public CalendarAdapter(){}

    // количество элементов
    @Override
    public int getCount() {
        return mCalendarList.size();
    }


    // Получить элемент по позиции
    @Override
    public Object getItem(int position) {
        return mCalendarList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
