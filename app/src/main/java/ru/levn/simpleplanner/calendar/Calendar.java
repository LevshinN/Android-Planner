package ru.levn.simpleplanner.calendar;

/**
 * Created by Levshin_N on 24.07.2015.
 */

public class Calendar {
    private String name;
    private String id;
    private boolean enabled;

    public Calendar( String newName, String newId, boolean isEnabled) {
        name = newName;
        id = newId;
        enabled = isEnabled;
    }

    public Calendar( String newName, String newId) {
        name = newName;
        id = newId;
        enabled = true;
    }

    public String getName() { return name; }
    public String getId() { return id; }
    public boolean isEnabled() { return enabled; }

    public void setEnabled( boolean isEnable ) {
        enabled = isEnable;
    }
}
