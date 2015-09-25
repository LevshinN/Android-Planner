package ru.levn.simpleplanner.planner;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 25.09.2015.
 */
public class EventBox {
    public int yStart;
    public int yEnd;
    public int columnNum;

    public String text;
    public String eventId;
    public int color;

    public boolean isLast = true;

    public EventBox(int yStart, int yEnd, String eventId, String text, int color) {
        this.yStart = yStart;
        this.yEnd = yEnd;
        this.eventId = eventId;
        this.text = text;
        this.color = color;
    }
}
