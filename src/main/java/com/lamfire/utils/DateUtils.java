package com.lamfire.utils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DateUtils {

    public static final TimeZone UTC_TIME_ZONE     = TimeZone.getTimeZone("GMT");
    public static final TimeZone TIMEZONE_CHINA    = TimeZone.getTimeZone("Asia/Shanghai");
    public static final long     MILLIS_PER_SECOND = 1000L;
    public static final long     MILLIS_PER_MINUTE = 60000L;
    public static final long     MILLIS_PER_HOUR   = 3600000L;
    public static final long     MILLIS_PER_DAY    = 86400000L;
    public static final int      SEMI_MONTH        = 1001;
    private static final int[][] fields            = { { 14 }, { 13 }, { 12 }, { 11, 10 }, { 5, 5, 9 }, { 2, 1001 },
            { 1 }, { 0 }                          };
    public static final int      WEEK_SUNDAY       = 1;
    public static final int      WEEK_MONDAY       = 2;
    public static final int      WEEK_TUESDAY      = 3;
    public static final int      WEEK_WEDNESDAY    = 4;
    public static final int      WEEK_THURSDAY     = 5;
    public static final int      WEEK_FRIDAY       = 6;
    public static final int      WEEK_SATURDAY     = 7;

    private static AtomicInteger atomic            = new AtomicInteger(0);
    private static long          lastMillis        = 0;

    public static void setYearMonthDay(Date date, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date.getTime());
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, day);
        date.setTime(c.getTimeInMillis());
    }

    public static void setHourMinuteSecond(Date date, int hour, int minute, int second) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date.getTime());
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        date.setTime(c.getTimeInMillis());
    }

    public static Date getDate(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
        Calendar calendar = new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute, second);
        return calendar.getTime();
    }

    public static long getTimeInMillis(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
        Calendar calendar = new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute, second);
        return calendar.getTimeInMillis();
    }

    public static long getTimeInMillis(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second,
                                       int millis) {
        Calendar calendar = new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute, second);
        return calendar.getTimeInMillis() + millis;
    }

    public static void setYear(Date date, int year) {
        date.setTime(setYear(date.getTime(), year));
    }

    public static void setMonth(Date date, int month) {
        date.setTime(setMonth(date.getTime(), month));
    }

    public static void setDay(Date date, int day) {
        date.setTime(setDay(date.getTime(), day));
    }

    public static void setHour(Date date, int hour) {
        date.setTime(setHour(date.getTime(), hour));
    }

    public static void setMinute(Date date, int minute) {
        date.setTime(setMinute(date.getTime(), minute));
    }

    public static void setSecond(Date date, int second) {
        date.setTime(setSecond(date.getTime(), second));
    }

    public static long setYear(long millis, int year) {
        return set(millis, Calendar.YEAR, year);
    }

    public static long setMonth(long millis, int month) {
        return set(millis, Calendar.MONTH, month - 1);
    }

    public static long setDay(long millis, int day) {
        return set(millis, Calendar.DAY_OF_MONTH, day);
    }

    public static long setHour(long millis, int hour) {
        return set(millis, Calendar.HOUR_OF_DAY, hour);

    }

    public static long setMinute(long millis, int minute) {
        return set(millis, Calendar.MINUTE, minute);
    }

    public static long setSecond(long millis, int second) {
        return set(millis, Calendar.SECOND, second);
    }

    public static int get(long millis, int field, TimeZone timeZone) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(timeZone);
        c.setTimeInMillis(millis);
        return c.get(field);
    }

    public static long set(long millis, int field, int value) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        c.set(field, value);
        return c.getTimeInMillis();
    }

    public static long set(long millis, int field, int value, TimeZone timeZone) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(timeZone);
        c.setTimeInMillis(millis);
        c.set(field, value);
        return c.getTimeInMillis();
    }

    public static int getSecond(long millis, TimeZone timeZone) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(timeZone);
        c.setTimeInMillis(millis);
        return c.get(Calendar.SECOND);
    }

    public static int getMinute(long millis, TimeZone timeZone) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(timeZone);
        c.setTimeInMillis(millis);
        return c.get(Calendar.MINUTE);
    }

    public static int getHour(long millis, TimeZone timeZone) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(timeZone);
        c.setTimeInMillis(millis);
        return c.get(Calendar.HOUR_OF_DAY);
    }

    public static int get(long millis, int field) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        return c.get(Calendar.YEAR);
    }

    public static int getSecond(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        return c.get(Calendar.SECOND);
    }

    public static int getMinute(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        return c.get(Calendar.MINUTE);
    }

    public static int getHour(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        return c.get(Calendar.HOUR_OF_DAY);
    }

    public static int getYear(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        return c.get(Calendar.YEAR);
    }

    public static int getMonth(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        return c.get(Calendar.MONTH) + 1;
    }

    public static int getDay(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public static int getYear(long millis, TimeZone timeZone) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        c.setTimeZone(timeZone);
        return c.get(Calendar.YEAR);
    }

    public static int getMonth(long millis, TimeZone timeZone) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        c.setTimeZone(timeZone);
        return c.get(Calendar.MONTH) + 1;
    }

    public static int getDay(long millis, TimeZone timeZone) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        c.setTimeZone(timeZone);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public static synchronized long getSafeTime() {
        long time = System.currentTimeMillis();
        if (time <= lastMillis) {
            time += atomic.incrementAndGet();
        } else {
            atomic.set(0);
        }
        lastMillis = time;
        return lastMillis;
    }

    public static Date parse(long millis) {
        return new Date(millis);
    }

    /**
     * ���һ�����ʱ�������
     * 
     * @param millis
     * @return
     */
    public static long getFullDayMillis(long millis) {
        return millis - millis % DateUtils.MILLIS_PER_DAY + DateUtils.MILLIS_PER_DAY;
    }

    /**
     * ���һ�����ʱ�������
     * 
     * @param date
     * @return
     */
    public static long getFullDayMillis(Date date) {
        return getFullDayMillis(date.getTime());
    }

    /**
     * ������ʱ��ʱ�������
     * 
     * @param millis
     * @return
     */
    public static long getZeroHourMillis(long millis) {
        return millis - millis % DateUtils.MILLIS_PER_DAY;
    }

    /**
     * ������ʱ��ʱ�������
     * 
     * @param date
     * @return
     */
    public static long getZeroHourMillis(Date date) {
        return getZeroHourMillis(date.getTime());
    }

    public static int getDayOfWeek(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public static boolean isDayOfWeek(long millis, int dayOfWeek) {
        return getDayOfWeek(millis) == dayOfWeek;
    }

    public static boolean isInWorkdays(long millis) {
        int dayOfWeek = getDayOfWeek(millis);
        if (dayOfWeek >= WEEK_MONDAY && dayOfWeek <= WEEK_FRIDAY) {
            return true;
        }
        return false;
    }

    public static boolean isSunday(long millis) {
        return isDayOfWeek(millis, WEEK_SUNDAY);
    }

    public static boolean isMonday(long millis) {
        return isDayOfWeek(millis, WEEK_MONDAY);
    }

    public static boolean isTuesday(long millis) {
        return isDayOfWeek(millis, WEEK_TUESDAY);
    }

    public static boolean isWednesday(long millis) {
        return isDayOfWeek(millis, WEEK_WEDNESDAY);
    }

    public static boolean isThursday(long millis) {
        return isDayOfWeek(millis, WEEK_THURSDAY);
    }

    public static boolean isFriday(long millis) {
        return isDayOfWeek(millis, WEEK_FRIDAY);
    }

    public static boolean isSaturday(long millis) {
        return isDayOfWeek(millis, WEEK_SATURDAY);
    }

    public static int getDayOfWeek(long millis, TimeZone timeZone) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(timeZone);
        cal.setTimeInMillis(millis);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public static boolean isDayOfWeek(long millis, TimeZone timeZone, int dayOfWeek) {
        return getDayOfWeek(millis, timeZone) == dayOfWeek;
    }

    public static boolean isInWorkdays(long millis, TimeZone timeZone) {
        int dayOfWeek = getDayOfWeek(millis, timeZone);
        if (dayOfWeek > 1 && dayOfWeek < 7) {
            return true;
        }
        return false;
    }

    public static boolean isSunday(long millis, TimeZone timeZone) {
        return isDayOfWeek(millis, timeZone, WEEK_SUNDAY);
    }

    public static boolean isMonday(long millis, TimeZone timeZone) {
        return isDayOfWeek(millis, timeZone, WEEK_MONDAY);
    }

    public static boolean isTuesday(long millis, TimeZone timeZone) {
        return isDayOfWeek(millis, timeZone, WEEK_TUESDAY);
    }

    public static boolean isWednesday(long millis, TimeZone timeZone) {
        return isDayOfWeek(millis, timeZone, WEEK_WEDNESDAY);
    }

    public static boolean isThursday(long millis, TimeZone timeZone) {
        return isDayOfWeek(millis, timeZone, WEEK_THURSDAY);
    }

    public static boolean isFriday(long millis, TimeZone timeZone) {
        return isDayOfWeek(millis, timeZone, WEEK_FRIDAY);
    }

    public static boolean isSaturday(long millis, TimeZone timeZone) {
        return isDayOfWeek(millis, timeZone, WEEK_SATURDAY);
    }

    public static boolean isSameDay(Date date1, Date date2) {
        if ((date1 == null) || (date2 == null)) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if ((cal1 == null) || (cal2 == null)) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return (cal1.get(0) == cal2.get(0)) && (cal1.get(1) == cal2.get(1)) && (cal1.get(6) == cal2.get(6));
    }

    public static boolean isSameInstant(Date date1, Date date2) {
        if ((date1 == null) || (date2 == null)) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return date1.getTime() == date2.getTime();
    }

    public static boolean isSameInstant(Calendar cal1, Calendar cal2) {
        if ((cal1 == null) || (cal2 == null)) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return cal1.getTime().getTime() == cal2.getTime().getTime();
    }

    public static boolean isSameLocalTime(Calendar cal1, Calendar cal2) {
        if ((cal1 == null) || (cal2 == null)) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return (cal1.get(14) == cal2.get(14)) && (cal1.get(13) == cal2.get(13)) && (cal1.get(12) == cal2.get(12))
               && (cal1.get(10) == cal2.get(10)) && (cal1.get(6) == cal2.get(6)) && (cal1.get(1) == cal2.get(1))
               && (cal1.get(0) == cal2.get(0)) && (cal1.getClass() == cal2.getClass());
    }

    public static Date parse(String str, String[] parsePatterns) throws ParseException {
        if ((str == null) || (parsePatterns == null)) {
            throw new IllegalArgumentException("Date and Patterns must not be null");
        }

        SimpleDateFormat parser = null;
        ParsePosition pos = new ParsePosition(0);
        for (int i = 0; i < parsePatterns.length; i++) {
            if (i == 0) parser = new SimpleDateFormat(parsePatterns[0]);
            else {
                parser.applyPattern(parsePatterns[i]);
            }
            pos.setIndex(0);
            Date date = parser.parse(str, pos);
            if ((date != null) && (pos.getIndex() == str.length())) {
                return date;
            }
        }
        throw new ParseException("Unable to parse the date: " + str, -1);
    }

    public static Date parse(String dateText, String pattern) throws ParseException {
        if ((dateText == null) || (pattern == null)) {
            throw new IllegalArgumentException("Date and Pattern must not be null");
        }
        SimpleDateFormat parser = new SimpleDateFormat(pattern);
        return parser.parse(dateText);
    }

    public static Date addYears(Date date, int amount) {
        return add(date, 1, amount);
    }

    public static Date addMonths(Date date, int amount) {
        return add(date, 2, amount);
    }

    public static Date addWeeks(Date date, int amount) {
        return add(date, 3, amount);
    }

    public static Date addDays(Date date, int amount) {
        return add(date, 5, amount);
    }

    public static Date addHours(Date date, int amount) {
        return add(date, 11, amount);
    }

    public static Date addMinutes(Date date, int amount) {
        return add(date, 12, amount);
    }

    public static Date addSeconds(Date date, int amount) {
        return add(date, 13, amount);
    }

    public static Date addMilliseconds(Date date, int amount) {
        return add(date, 14, amount);
    }

    public static Date add(Date date, int calendarField, int amount) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    public static Date round(Date date, int field) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar gval = Calendar.getInstance();
        gval.setTime(date);
        modify(gval, field, true);
        return gval.getTime();
    }

    public static Calendar round(Calendar date, int field) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar rounded = (Calendar) date.clone();
        modify(rounded, field, true);
        return rounded;
    }

    public static Date round(Object date, int field) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        if ((date instanceof Date)) return round((Date) date, field);
        if ((date instanceof Calendar)) {
            return round((Calendar) date, field).getTime();
        }
        throw new ClassCastException("Could not round " + date);
    }

    public static Date truncate(Date date, int field) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar gval = Calendar.getInstance();
        gval.setTime(date);
        modify(gval, field, false);
        return gval.getTime();
    }

    public static Calendar truncate(Calendar date, int field) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar truncated = (Calendar) date.clone();
        modify(truncated, field, false);
        return truncated;
    }

    public static Date truncate(Object date, int field) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        if ((date instanceof Date)) return truncate((Date) date, field);
        if ((date instanceof Calendar)) {
            return truncate((Calendar) date, field).getTime();
        }
        throw new ClassCastException("Could not truncate " + date);
    }

    private static void modify(Calendar val, int field, boolean round) {
        if (val.get(1) > 280000000) {
            throw new ArithmeticException("Calendar value too large for accurate calculations");
        }

        if (field == 14) {
            return;
        }

        Date date = val.getTime();
        long time = date.getTime();
        boolean done = false;

        int millisecs = val.get(14);
        if ((!round) || (millisecs < 500)) {
            time -= millisecs;
            if (field == 13) {
                done = true;
            }

        }

        int seconds = val.get(13);
        if ((!done) && ((!round) || (seconds < 30))) {
            time -= seconds * 1000L;
            if (field == 12) {
                done = true;
            }

        }

        int minutes = val.get(12);
        if ((!done) && ((!round) || (minutes < 30))) {
            time -= minutes * 60000L;
        }

        if (date.getTime() != time) {
            date.setTime(time);
            val.setTime(date);
        }

        boolean roundUp = false;
        for (int i = 0; i < fields.length; i++) {
            for (int j = 0; j < fields[i].length; j++) {
                if (fields[i][j] != field) continue;
                if ((round) && (roundUp)) {
                    if (field == 1001) {
                        if (val.get(5) == 1) {
                            val.add(5, 15);
                        } else {
                            val.add(5, -15);
                            val.add(2, 1);
                        }
                    } else {
                        val.add(fields[i][0], 1);
                    }
                }
                return;
            }

            int offset = 0;
            boolean offsetSet = false;

            switch (field) {
                case 1001:
                    if (fields[i][0] != 5) {
                        break;
                    }
                    offset = val.get(5) - 1;

                    if (offset >= 15) {
                        offset -= 15;
                    }

                    roundUp = offset > 7;
                    offsetSet = true;
                    break;
                case 9:
                    if (fields[i][0] != 11) {
                        break;
                    }
                    offset = val.get(11);
                    if (offset >= 12) {
                        offset -= 12;
                    }
                    roundUp = offset > 6;
                    offsetSet = true;
            }

            if (!offsetSet) {
                int min = val.getActualMinimum(fields[i][0]);
                int max = val.getActualMaximum(fields[i][0]);

                offset = val.get(fields[i][0]) - min;

                roundUp = offset > (max - min) / 2;
            }

            if (offset != 0) {
                val.set(fields[i][0], val.get(fields[i][0]) - offset);
            }
        }
        throw new IllegalArgumentException("The field " + field + " is not supported");
    }

    public static Iterator<Calendar> iterator(Date focus, int rangeStyle) {
        if (focus == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar gval = Calendar.getInstance();
        gval.setTime(focus);
        return iterator(gval, rangeStyle);
    }

    public static Iterator<Calendar> iterator(Calendar focus, int rangeStyle) {
        if (focus == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar start = null;
        Calendar end = null;
        int startCutoff = 1;
        int endCutoff = 7;
        switch (rangeStyle) {
            case 5:
            case 6:
                start = truncate(focus, 2);

                end = (Calendar) start.clone();
                end.add(2, 1);
                end.add(5, -1);

                if (rangeStyle != 6) break;
                startCutoff = 2;
                endCutoff = 1;
                break;
            case 1:
            case 2:
            case 3:
            case 4:
                start = truncate(focus, 5);
                end = truncate(focus, 5);
                switch (rangeStyle) {
                    case 1:
                        break;
                    case 2:
                        startCutoff = 2;
                        endCutoff = 1;
                        break;
                    case 3:
                        startCutoff = focus.get(7);
                        endCutoff = startCutoff - 1;
                        break;
                    case 4:
                        startCutoff = focus.get(7) - 3;
                        endCutoff = focus.get(7) + 3;
                }

                break;
            default:
                throw new IllegalArgumentException("The range style " + rangeStyle + " is not valid.");
        }
        if (startCutoff < 1) {
            startCutoff += 7;
        }
        if (startCutoff > 7) {
            startCutoff -= 7;
        }
        if (endCutoff < 1) {
            endCutoff += 7;
        }
        if (endCutoff > 7) {
            endCutoff -= 7;
        }
        while (start.get(7) != startCutoff) {
            start.add(5, -1);
        }
        while (end.get(7) != endCutoff) {
            end.add(5, 1);
        }
        return new DateIterator(start, end);
    }

    public static Iterator<Calendar> iterator(Object focus, int rangeStyle) {
        if (focus == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        if ((focus instanceof Date)) return iterator((Date) focus, rangeStyle);
        if ((focus instanceof Calendar)) {
            return iterator((Calendar) focus, rangeStyle);
        }
        throw new ClassCastException("Could not iterate based on " + focus);
    }

    static class DateIterator implements Iterator<Calendar> {

        private final Calendar endFinal;
        private final Calendar spot;

        DateIterator(Calendar startFinal, Calendar endFinal) {
            this.endFinal = endFinal;
            this.spot = startFinal;
            this.spot.add(5, -1);
        }

        public boolean hasNext() {
            return this.spot.before(this.endFinal);
        }

        public Calendar next() {
            if (this.spot.equals(this.endFinal)) {
                throw new NoSuchElementException();
            }
            this.spot.add(5, 1);
            return (Calendar) this.spot.clone();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
