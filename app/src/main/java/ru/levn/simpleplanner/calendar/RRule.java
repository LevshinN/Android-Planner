package ru.levn.simpleplanner.calendar;

import android.util.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import ru.levn.simpleplanner.Common;
import ru.levn.simpleplanner.R;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 31.08.2015.
 */
public class RRule  {
    private enum ERepeatMode { RM_DAILY, RM_WEEKLY, RM_MONTHLY, RM_YEARLY }
    public enum EEndMode {EM_FOREVER, EM_COUNT, EM_UNTIL}

    // Совершается ли сейчас процесс парсинга правила
    private boolean isParsingMode = false;

    // ------ Параметры, присутствуюшие в любом правиле ------

    // Частота повтора
    private ERepeatMode repeatMode;

    // Как задана граница завершения повторения (задана ли вообще?)
    private EEndMode endMode = EEndMode.EM_FOREVER;

    // Время начала события
    private long utcStart;


    // ------ Параметры, опциональные для всех типов повторения ------

    // Интервал повтора (каждые 2 дня, каждые 3 месяца и т.п.
    private int mInterval = 1;

    // Количество раз, которое нужно повторить событие
    private int mCount;

    // Дата, до которой нужно повторять событие в таймзоне UTC
    private String mUntil;

    // ------ Параметры опциональные для отдельных режимов ------

    // --- Режим WEEKLY ---

    // Дни недели, по которым нудно повторять
    private String weeklyByDay;


    // --- Режим MONTHLY ---

    // День месяца, в который нужно повторять
    private String monthlyByMonthDay;

    // Конкретный день конкретной недели месяца
    private String monthlyByDay;


    // --- Режим YEARLY ---

    // В какой месяц повторять
    private String[] yearlyByMonth;

    // В какой день повторять, если не указан, то выбирается день месяца из даты начала событий
    private String yearlyByDay;




    public void parse( String rRule ) throws ParseException {
        isParsingMode = true;

        ArrayList<Pair<String, String>> rules = mGetRules(rRule);
        for (Pair<String, String> rule : rules) {
            switch (rule.first) {
                case "FREQ":
                    setFreq(rule.second);
                    break;
                case "INTERVAL":
                    setInterval(rule.second);
                    break;
                case "COUNT":
                    setCount(rule.second);
                    break;
                case "UNTIL":
                    setUntil(rule.second);
                    break;
                case "BYDAY":
                    setByDay(rule.second);
                    break;
                case "BYMONTHDAY":
                    setByMonthDay(rule.second);
                    break;
                case "BYMONTH":
                    setByMonth(rule.second);
                    break;
            }
        }
        isParsingMode = false;
    }

    private ArrayList<Pair<String,String>> mGetRules( String rRule ) throws ParseException {
        ArrayList<Pair<String, String>> rules = new ArrayList<>();

        String[] rulesList = rRule.split(";");
        int index = 0;
        for (String rule : rulesList) {
            if (rule.length() == 0) {
                throw new ParseException("Empty rRule.", index);
            }

            String[] param = rule.split("=");

            if (param.length > 2) {
                index += param[0].length() + 1 + param[1].length();
                throw new ParseException("Incorrect rule, found more than 1 \"equal\" char.", index);
            }

            if (param.length < 2) {
                throw new ParseException("Rule without params was found", index);
            }

            rules.add(new Pair<>(param[0], param[1]));
        }

        return rules;
    }

    public String getDescription()  {
        String description = "";
        switch (repeatMode) {
            case RM_YEARLY:
                description += Common.sGetResources().getString(R.string.rr_every_year);
                break;
            case RM_MONTHLY:
                description += Common.sGetResources().getString(R.string.rr_every_month);
                break;
            case RM_WEEKLY:
                description += Common.sGetResources().getString(R.string.rr_every_week);
                break;
            case RM_DAILY:
                description += Common.sGetResources().getString(R.string.rr_every_day);
                break;
            default:
                break;
        }

        if (endMode != EEndMode.EM_FOREVER) {
            switch (endMode) {
                case EM_COUNT:
                    String times;
                    if (mCount % 10 <= 4 && mCount % 10 >= 2 && mCount / 10 != 1) {
                        times = Common.sGetResources().getString(R.string.rr_times_2_4);
                    } else {
                        times = Common.sGetResources().getString(R.string.rr_times_usual);
                    }
                    description += '\n' + String.valueOf(mCount) +  ' ' + times;
                    break;
                case EM_UNTIL:
                    Calendar c = new GregorianCalendar();
                    c.set(Integer.valueOf(mUntil.substring(0, 4)),
                            Integer.valueOf(mUntil.substring(4,6)),
                            Integer.valueOf(mUntil.substring(6,8)),
                            Integer.valueOf(mUntil.substring(9, 11)),
                            Integer.valueOf(mUntil.substring(11, 13)),
                            Integer.valueOf(mUntil.substring(13, 15)));
                    description += '\n' + Common.sGetResources().getString(R.string.rr_until)
                            + ' ' + CalendarProvider.getDate(c.getTimeInMillis());
                    break;
            }
        }

        if (mInterval > 1) {
            description += '\n';
            description += Common.sGetResources().getString(R.string.rr_interval)
                    + ' ' + String.valueOf(mInterval);
        }


        return description;
    }

    public String getRule() {

        String rule = "FREQ=";
        switch (repeatMode) {
            case RM_YEARLY:
                rule += "YEARLY;";
                break;
            case RM_MONTHLY:
                rule += "MONTHLY;";
                break;
            case RM_WEEKLY:
                rule += "WEEKLY;";
                break;
            case RM_DAILY:
                rule += "DAILY;";
                break;
            default:
                break;
        }


        if (mInterval > 1) {
            rule += "INTERVAL=" + String.valueOf(mInterval) + ";";
        }

        switch (endMode) {
            case EM_UNTIL:
                rule += "UNTIL=" + mUntil + ";";
                break;
            case EM_COUNT:
                rule += "COUNT=" + String.valueOf(mCount) + ";";
                break;
            default:
                break;
        }

        return rule;

    }

    public void setStart( long utcStart ) {
        this.utcStart = utcStart;
    }

    public void setFreq( String freqMode ) {
        switch (freqMode) {
            case "YEARLY":
                repeatMode = ERepeatMode.RM_YEARLY;
                return;
            case "MONTHLY":
                repeatMode = ERepeatMode.RM_MONTHLY;
                return;
            case "WEEKLY":
                repeatMode = ERepeatMode.RM_WEEKLY;
                return;
            case "DAILY":
                repeatMode = ERepeatMode.RM_DAILY;
                return;
            default:
                throw new IllegalArgumentException("Unknown value " + freqMode);
        }
    }

    public void setInterval( String interval ) {
        mInterval = Integer.valueOf(interval);
    }

    public void setCount( String count ) {
        if (isParsingMode && endMode != EEndMode.EM_FOREVER) {
            throw new IllegalArgumentException ("Rule can contain only one of rules: COUNT or UNTIL");
        } else {
            endMode = EEndMode.EM_COUNT;
        }
        mCount = Integer.valueOf(count);
    }

    public void setUntil( String until ) {
        if (isParsingMode && endMode != EEndMode.EM_FOREVER) {
            throw new IllegalArgumentException("RRule can contain only one of rules: COUNT or UNTIL");
        } else {
            endMode = EEndMode.EM_UNTIL;
        }
        mUntil = until;
    }

    public void setByDay( String rule ) {
        if ( null == repeatMode ) {
            throw new IllegalArgumentException("Repeat mode has to be set before BYDAY");
        } else {
            switch(repeatMode) {
                case RM_WEEKLY:
                    weeklyByDay = rule;
                    break;
                case RM_MONTHLY:
                    monthlyByDay = rule;
                    break;
                case RM_YEARLY:
                    yearlyByDay = rule;
                    break;
                default:
                    throw new IllegalArgumentException("Repeat mode "
                            + repeatMode.name() + "incompatible with BYDAY rule");
            }
        }

    }

    public void setByMonthDay( String rule ) {
        if (repeatMode != ERepeatMode.RM_MONTHLY) {
            throw new IllegalArgumentException("Rule BYMONTHDAY incompatible with"
                    + repeatMode.name() + " repeat mode.");
        } else {
            monthlyByMonthDay = rule;
        }
    }

    public void setByMonth( String rule ) {
        if (repeatMode != ERepeatMode.RM_YEARLY) {
            throw new IllegalArgumentException("Rule BYMONTH incompatible with"
                    + repeatMode.name() + " repeat mode.");
        } else {
            yearlyByMonth = rule.split(",");
        }
    }
}
