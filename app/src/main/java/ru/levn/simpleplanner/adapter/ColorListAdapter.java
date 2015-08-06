package ru.levn.simpleplanner.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import ru.levn.simpleplanner.R;


/**
 * Created by Levshin_N on 04.08.2015.
 */
public class ColorListAdapter extends BaseAdapter {

    private LayoutInflater lInflater;
    private int[] colorList;
    private String[] colorsNamesList;

    public ColorListAdapter(Context context, int[] colors, String[] colorsNames) {
        colorList = colors;
        colorsNamesList = colorsNames;
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return colorList.length;
    }

    @Override
    public Object getItem(int position) {
        return colorList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.color_item, parent, false);
        }

        ImageView circle = (ImageView)view.findViewById(R.id.color_sample);
        TextView colorName = (TextView)view.findViewById(R.id.color_name);


        GradientDrawable bgShape = (GradientDrawable)circle.getBackground();
        bgShape.setColor(colorList[position]);
        colorName.setText(colorsNamesList[position]);

        return view;
    }
}
