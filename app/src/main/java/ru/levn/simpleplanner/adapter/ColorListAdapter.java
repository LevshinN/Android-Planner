package ru.levn.simpleplanner.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ru.levn.simpleplanner.R;


/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 04.08.2015.
 */
public class ColorListAdapter extends BaseAdapter {

    private LayoutInflater mLInflater;
    private int[] mColorList;
    private String[] mColorNamesList;

    public ColorListAdapter(Context context, int[] colors, String[] colorsNames) {
        mColorList = colors;
        mColorNamesList = colorsNames;
        mLInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mColorList.length;
    }

    @Override
    public Object getItem(int position) {
        return mColorList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = mLInflater.inflate(R.layout.color_item, parent, false);
        }

        ImageView circle = (ImageView)view.findViewById(R.id.color_sample);
        TextView colorName = (TextView)view.findViewById(R.id.color_name);


        GradientDrawable bgShape = (GradientDrawable)circle.getBackground();
        bgShape.setColor(mColorList[position]);
        colorName.setText(mColorNamesList[position]);

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        View v = null;

        // If this is the initial dummy entry, make it hidden
        if (position == 0) {
            TextView tv = new TextView(mLInflater.getContext());
            tv.setHeight(0);
            tv.setVisibility(View.GONE);
            v = tv;
        }
        else {
            // Pass convertView as null to prevent reuse of special case views
            v = super.getDropDownView(position, null, parent);
        }

        // Hide scroll bar because it appears sometimes unnecessarily, this does not prevent scrolling
        parent.setVerticalScrollBarEnabled(false);
        return v;
    }
}
