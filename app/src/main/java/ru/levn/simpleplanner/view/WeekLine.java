package ru.levn.simpleplanner.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import ru.levn.simpleplanner.R;
import ru.levn.simpleplanner.calendar.CalendarProvider;
import ru.levn.simpleplanner.calendar.Event;

/**
 * Автор: Левшин Николай, 707 группа.
 * Дата создания: 10.09.2015.
 */
public class WeekLine{
    // Границы расположения текущей клетки
    float startX;
    float startY;
    float endX;
    float endY;

    int currentMonth;

    private int cellActiveColor;
    private int cellPassiveColor;
    private int weekColor;
    private int backgroundColor;
    private int pressedBackgroundColor;
    private int numberFontSize;
    private int marginHorizontal = 1;
    private int marginVertical = 1;
    private int maxLinesNum;

    private float weekNumberCellWidth;
    private float dayCellWidth;
    private float height;
    private float width;
    private float textHeight;
    private float lineHeight;

    Calendar representTime;

    private ArrayList<Event> events;
    boolean[][] reservedLines;

    // Счетчики непоместившихся на экран событий.
    int[] hiddenEventsNum = new int[7];
    int touchedCell = -1;

    private RectF lineRect = new RectF();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public WeekLine(Context context) {

        cellActiveColor = context.getResources().getColor(android.R.color.black);
        cellPassiveColor = context.getResources().getColor(android.R.color.darker_gray);
        weekColor = context.getResources().getColor(R.color.red);
        backgroundColor = context.getResources().getColor(R.color.btn_background);
        pressedBackgroundColor = context.getResources().getColor(R.color.btn_pressed_background);
        numberFontSize = context.getResources().getDimensionPixelSize(R.dimen.abc_text_size_body_1_material);
    }

    void setBounds(float startX, float endX, float startY, float endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;

        /*
        С целью ускорения отрисовки не будем делать адаптивную верску а
        просто  зададим ширину клеток. Так как дней в неделе 7 и ещё одна клетка
        для обозначения недели, которая должна быть значительно меньше, то пусть
        их соотношение длинн будет 3:1, тогда делим всю длинну на 7х3+1=22 кусочка.
         */

        width = endX - startX;
        height = endY - startY;

        weekNumberCellWidth = width / 22;
        dayCellWidth = weekNumberCellWidth * 3;

        paint.setTextSize(numberFontSize);

        textHeight = paint.descent() - paint.ascent();
    }

    void setEvents(ArrayList<Event> events) {
        ArrayList<Event> sortedEvents = new ArrayList<>();
        for (Event event : events) {
            int i;
            for ( i = 0; i < sortedEvents.size(); ++ i) {
                Event tmp = sortedEvents.get(i);
                if ((event.timeEnd - event.timeStart) > (tmp.timeEnd - tmp.timeStart)) {
                    sortedEvents.add(i, event);
                    break;
                }
            }
            if (i == sortedEvents.size()) {
                sortedEvents.add(event);
            }
        }
        this.events = sortedEvents;
    }

    public void draw(Canvas canvas) {

        canvas.save();
        canvas.translate((int) startX, (int) startY);
        {
            drawTable(canvas);
            drawEvents(canvas);
            drawHiddenEvents(canvas);
        }
        canvas.restore();
    }

    private void drawTable(Canvas canvas) {

        // Рисуем разделительную линию
        paint.setColor(cellActiveColor);
        canvas.drawLine(weekNumberCellWidth, 0, width, 0, paint);

        String number = String.valueOf(representTime.get(Calendar.WEEK_OF_YEAR));

        paint.setColor(weekColor);

        int xPos = (int)(weekNumberCellWidth - paint.measureText(number)) / 2;
        int yPos = (int) (textHeight) ;

        canvas.drawText(number, xPos, yPos, paint);

        Calendar c = (Calendar)representTime.clone();

        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        c.getTimeInMillis();

        for (int i = 0; i < 7; ++i) {

            if (i == touchedCell) {
                paint.setColor(pressedBackgroundColor);
                RectF rectF = new RectF(
                        weekNumberCellWidth + i * dayCellWidth,
                        0,
                        weekNumberCellWidth + (i+1)*dayCellWidth,
                        height
                );
                canvas.drawRect(rectF, paint);
            }

            if (c.get(Calendar.MONTH) == currentMonth) {
                paint.setColor(cellActiveColor);
            } else {
                paint.setColor(cellPassiveColor);
            }

            number = String.valueOf(c.get(Calendar.DAY_OF_MONTH));

            xPos = (int)(weekNumberCellWidth + i * dayCellWidth + paint.descent());
            yPos = (int) textHeight;

            canvas.drawText(number, xPos, yPos,paint);

            c.add(Calendar.DAY_OF_YEAR, 1);
            c.getTimeInMillis();
        }
    }

    private void drawEvents(Canvas canvas) {

        // Т.к. нужно оставить место под номер и слово Ещё,то одну строчку резервируем

        lineHeight = textHeight * 1.5f;
        maxLinesNum = (int)(height / lineHeight - 1);

        /* Строим вспомогательную таблицу по которой будем определять где есть свободное место.
        Идея заключается в том, что мы будем опускать линию события на более нижний уровень до тех
        пор, пока она веся не уместится на этом уровне (не будет пересекать уже размещенные события.
        Соостветственно в этой таблице значение ячейки говорит нам, расположено ли уже какое-то
        события в данном столбце на данном уровне.
        */

        reservedLines = new boolean[7][maxLinesNum];
        for (int i = 0; i < 7; ++i) {
            Arrays.fill(reservedLines[i], false);
        }

        Arrays.fill(hiddenEventsNum, 0);

        for (Event event : events) {

            Pair<Integer, Integer> weekDays = getEventWeekDays(event);

            int lineLevel = 0;
            // Опускаем нашу линию события на уровень ниже, пока она полностью не поместится.
            while (lineLevel < maxLinesNum) {
                if (isGoodLevel(weekDays.first, weekDays.second, lineLevel)) break;
                lineLevel += 1;
            }

            // Если предыдущий этап завершился успешно и уровень нашелся.
            if (lineLevel < maxLinesNum) {

                // Резервируем участок
                reserveLine(weekDays.first, weekDays.second, lineLevel);

                // Рисуем линию
                drawEventLine(canvas, weekDays.first, weekDays.second, lineLevel, event.color, event.title);
            } else {
                // Иначе добавляем в соответствующие ячейки ещё одно невместившееся событие.
                addHiddenEvent(weekDays.first, weekDays.second);
            }
        }
    }

    private void addHiddenEvent(int start, int end) {
        for (int i = start; i <=end; ++i) {
            hiddenEventsNum[i] += 1;
        }
    }

    private Pair<Integer, Integer> getEventWeekDays(Event e) {

        Pair<Long, Long> borders;
        Calendar c = new GregorianCalendar();

        if (e.isAllDay) {
            c.setTimeZone(TimeZone.getTimeZone("UTC"));
            borders = CalendarProvider.getWeekPeriod(representTime.getTimeInMillis(), true);
        } else {
            c.setTimeZone(TimeZone.getDefault());
            borders = CalendarProvider.getWeekPeriod(representTime.getTimeInMillis(), false);
        }

        c.setTimeInMillis(Math.max(e.timeStart, borders.first));
        int dayStart = (c.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 7) % 7;

        c.setTimeInMillis(Math.min(e.timeEnd - 1, borders.second - 1));
        int dayEnd = (c.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 7) % 7;

        return new Pair<>(dayStart, dayEnd);
    }

    private boolean isGoodLevel(int start, int end, int level) {
        for (int i = start; i <= end; ++i) {
            if (reservedLines[i][level]) return false;
        }
        return true;
    }

    private void reserveLine(int start, int end, int lineLevel) {
        for (int i = start; i <=end; ++i) {
            reservedLines[i][lineLevel] = true;
        }
    }

    private void drawHiddenEvents(Canvas canvas) {
        Calendar c = (Calendar)representTime.clone();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        c.getTimeInMillis();

        int xPos;
        int yPos;

        String hiddenNumber;

        for (int i = 0; i < 7; ++i) {
            if (hiddenEventsNum[i] > 0) {

                if (i == touchedCell) {
                    paint.setColor(pressedBackgroundColor);
                } else {
                    paint.setColor(backgroundColor);
                }

                canvas.drawRect(weekNumberCellWidth + i * dayCellWidth - marginHorizontal,
                        lineHeight * (maxLinesNum),
                        weekNumberCellWidth + (i + 1) * dayCellWidth + marginHorizontal,
                        lineHeight * (maxLinesNum + 1),
                        paint);

                if (c.get(Calendar.MONTH) == currentMonth) {
                    paint.setColor(cellActiveColor);
                } else {
                    paint.setColor(cellPassiveColor);
                }

                if (reservedLines[i][maxLinesNum - 1]) {
                    hiddenNumber = "Ещё +" + String.valueOf(hiddenEventsNum[i] + 1);
                } else {
                    hiddenNumber = "+" + String.valueOf(hiddenEventsNum[i]);
                }



                xPos = (int)(weekNumberCellWidth + i * dayCellWidth + dayCellWidth / 2 - paint.measureText(hiddenNumber) / 2);
                yPos = (int) ((maxLinesNum) * lineHeight + lineHeight / 2 + textHeight / 2);

                canvas.drawText(hiddenNumber, xPos, yPos, paint);
            }
            c.add(Calendar.DAY_OF_YEAR, 1);
            c.getTimeInMillis();
        }
    }

    private void drawEventLine(Canvas canvas, int start, int end, int level, int color, String text) {
        paint.setColor(0x88000000 + color);
        lineRect.set(
                weekNumberCellWidth + start * dayCellWidth + marginHorizontal,
                lineHeight * (level + 1) + marginVertical,
                weekNumberCellWidth + (end + 1) * dayCellWidth - marginHorizontal,
                lineHeight * (level + 2) - marginVertical);
        canvas.drawRect(lineRect, paint);

        if(text != null && !text.equals("")) {
            paint.setColor(cellActiveColor);
            String header = text.split("\n")[0];

            header = cutString(header, dayCellWidth - 2 * marginHorizontal, paint);

            int posX = (int)(weekNumberCellWidth + start * dayCellWidth + 4 * marginHorizontal);
            int posY = (int)(lineHeight * (level + 1) + lineHeight / 2 - (paint.descent() + paint.ascent()) / 2);
            canvas.drawText(header, posX, posY, paint);
        }
    }

    private String cutString(String s, float width, Paint p) {

        if (p.measureText(s) < width) return s;

        // Пробуем разбить по пробелам
        boolean isNotEmpty = false;
        int biggestLength = 0;
        for(int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == ' ' && isNotEmpty) {
                if (p.measureText(s.substring(0, i)) > width) break;
                biggestLength = i;
            } else {
                isNotEmpty = true;
            }
        }

        if (biggestLength > 0) return s.substring(0, biggestLength);

        // Бинарным поиском ищем наибольшее количество символов
        while(p.measureText(s.substring(0, biggestLength)) < width) ++biggestLength;
        return s.substring(0, biggestLength - 1);
    }

    public float getLineHeight() {
        return lineHeight;
    }

    public void touchCell(float x) {
        touchedCell = (int)((x - weekNumberCellWidth) / dayCellWidth);
        if (touchedCell < 0 || touchedCell > 6) {
            touchedCell = -1;
        }

    }
}
