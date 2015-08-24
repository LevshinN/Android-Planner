package ru.levn.simpleplanner.fragment;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;

import ru.levn.simpleplanner.Common;
import ru.levn.simpleplanner.R;
import ru.levn.simpleplanner.adapter.EventAdapter;
import ru.levn.simpleplanner.calendar.CalendarProvider;
import ru.levn.simpleplanner.calendar.Event;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 21.08.2015.
 */

public class PageWeek extends ModeFragment {

    private ViewGroup mRootView;

    static final String ARGUMENT_REPRESENT_DATE = "arg_represent_date";
    long representTime;

    public boolean changed;
    public boolean ready;

    int[] dayNamesProjection = {2,3,4,5,6,7,1};

    public static PageWeek newInstance(long date) {
        PageWeek pageWeek = new PageWeek();
        Bundle arguments = new Bundle();
        arguments.putLong(ARGUMENT_REPRESENT_DATE, date);
        pageWeek.setArguments(arguments);
        return pageWeek;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        representTime = getArguments().getLong(ARGUMENT_REPRESENT_DATE);
        changed = false;
        ready = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = (ViewGroup)inflater.inflate(R.layout.week_page, container, false);

        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Common.sFragWidth = getView().getWidth();
                Common.sFragHeight = getView().getHeight();
                if (Common.sFragWidth > 0) {
                    removeOnGlobalLayoutListener(getView(), this);
                    onBuild();
                }
            }
        });

        return mRootView;
    }

    AdapterView.OnItemClickListener mSelectItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Event event = (Event)parent.getItemAtPosition(position);
            EventInfo event_info = EventInfo.newInstance(0, event);
            event_info.show(getFragmentManager(), "event_info");
        }
    };


    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < 16) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    @Override
    public void onUpdate() {

//        Pair<Long,Long> period = CalendarProvider.getWeekPeriod(representTime);
//
//        long start = period.first;
//        long end = period.second;
//        long dayDuration = (end - start) / 7;
//
//        DateFormatSymbols symbols = DateFormatSymbols.getInstance();
//        String[] dayNames = symbols.getShortWeekdays();
//
//        for (int i = 0; i < 7; ++i) {
//            View currentCard = mRootView.findViewById(WEEK_IDS[i]);
//
//            ((TextView) currentCard.findViewById(R.id.week_card_description)).setText(
//                    dayNames[dayNamesProjection[i]] + " "
//                            + CalendarProvider.getDate(start + dayDuration * i));

            //ArrayList<Event> events = Common.sEvents.getDayEvents(start + dayDuration * i );
            //EventWeekAdapter adapter = new EventWeekAdapter(this.getActivity(), events);
            //ListView lv = (ListView)currentCard.findViewById(R.id.week_card_list);
            //lv.setAdapter(adapter);
            //lv.setOnItemClickListener(mSelectItemListener);
        //}
    }

    @Override
    public void onBuild() {


        // Т.к в grid view элементы добавляются наверх, проходимся в обратном порядке
        for (int i = 6; i != -1; --i) {
            mRootView.addView(getCard());
        }

        //onUpdate();
    }

    private CardView getCard() {

        int fragmentWidth = Common.sFragWidth;
        int fragmentHeight = Common.sFragHeight;

        int cardWidth;
        int cardHeight;

        if (fragmentWidth > fragmentHeight) {
            cardWidth = fragmentWidth / 4;
            cardHeight = fragmentHeight / 2;
        } else {
            cardWidth = fragmentWidth / 2;
            cardHeight = fragmentHeight / 4;
        }

        CardView cv = new CardView(getActivity());
        cv.setLayoutParams(new LinearLayout.LayoutParams(cardWidth, cardHeight));
        cv.setCardElevation(2 * Common.sScreenDensity);
        cv.setRadius(2 * Common.sScreenDensity);

        LinearLayout ll = new LinearLayout(getActivity());
        ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        ll.setOrientation(LinearLayout.VERTICAL);

        TextView cardHead = new TextView(getActivity());
        cardHead.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        ListView eventList = new ListView(getActivity());
        eventList.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1.0f));

        ViewHolder vh = new ViewHolder();
        vh.header = cardHead;
        vh.events = eventList;

        ll.addView(eventList);
        ll.addView(cardHead);
        cv.addView(ll);
        cv.setTag(vh);

        return cv;
    }

    static class ViewHolder {
        TextView header;
        ListView events;
    }
}

class EventWeekAdapter extends EventAdapter {
    public EventWeekAdapter(Context context, ArrayList<Event> events) {
        mEventList = events;
        mLInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = mLInflater.inflate(R.layout.week_event, parent, false);
        }

        Event event = (Event) getItem(position);

        GradientDrawable bgShape = (GradientDrawable) view.findViewById(R.id.week_event_color).getBackground();
        if (event.color != 0) {
            bgShape.setColor(0xff000000 + event.color);
        } else {
            bgShape.setColor(view.getContext().getResources().getColor(R.color.default_color));
        }

        ((TextView)view.findViewById(R.id.week_event_title)).setText(event.title);
        ((TextView)view.findViewById(R.id.week_event_time)).setText(event.getTextDate(false));

        return view;
    }
}
