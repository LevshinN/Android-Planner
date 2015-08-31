package ru.levn.simpleplanner.calendar;

import java.text.ParseException;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 31.08.2015.
 */
public class RRule  {
    private enum ERepeatMode { RM_DAILY, RM_WEEKLY, RM_MONTHLY, RM_YEARLY }
    private String weekStart;
    private ERepeatMode repeatMode;

    public void parse(String rRule) throws ParseException {
        String[] rules = rRule.split(";");
        int index = 0;
        for (String rule : rules) {
            if (rule.length() == 0) {
                throw new ParseException("Empty rule", index);
            }

            String[] param = rule.split("=");

            if (param.length > 2) {
                index += param[0].length() + 1 + param[1].length();
                throw new ParseException("Incorrect rule, found more than 1 \"equal\" char", index);
            }

            if (param.length < 2) {
                throw new ParseException("Rule without params was found", index);
            }

            switch (param[0]) {
                case "FREQ":
                    if (!setFreq(param[1])) {
                        throw new ParseException("Unnown param for FREQ: " + param[0], index + param[0].length() + 1);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public String getDescription() {
        String description = "";
        switch (repeatMode) {
            case RM_YEARLY:
                description += "Every year";
                break;
            case RM_MONTHLY:
                description += "Every month";
                break;
            case RM_WEEKLY:
                description += "Every week";
                break;
            case RM_DAILY:
                description += "Every day";
                break;
            default:
                break;
        }
        return description;
    }

    private boolean setFreq(String freqMode) {
        switch (freqMode) {
            case "YEARLY":
                repeatMode = ERepeatMode.RM_YEARLY;
                return true;
            case "MONTHLY":
                repeatMode = ERepeatMode.RM_MONTHLY;
                return true;
            case "WEEKLY":
                repeatMode = ERepeatMode.RM_WEEKLY;
                return true;
            case "DAILY":
                repeatMode = ERepeatMode.RM_DAILY;
                return true;
            default:
                return false;
        }
    }
}
