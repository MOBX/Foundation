package com.lamfire.json.serializer;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import com.lamfire.json.util.CharUtils;

public class DateSerializer implements ObjectSerializer {

    public final static DateSerializer instance = new DateSerializer();

    public void write(JSONSerializer serializer, Object object) throws IOException {
        SerializeWriter out = serializer.getWriter();

        if (object == null) {
            out.writeNull();
            return;
        }

        Date date = (Date) object;
        long time = date.getTime();
        if (serializer.isEnabled(SerializerFeature.UseISO8601DateFormat)) {
            if (serializer.isEnabled(SerializerFeature.UseSingleQuotes)) {
                out.append('\'');
            } else {
                out.append('\"');
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            int millis = calendar.get(Calendar.MILLISECOND);

            char[] buf;
            if (millis != 0) {
                buf = "0000-00-00T00:00:00.000".toCharArray();
                CharUtils.getChars(millis, 23, buf);
                CharUtils.getChars(second, 19, buf);
                CharUtils.getChars(minute, 16, buf);
                CharUtils.getChars(hour, 13, buf);
                CharUtils.getChars(day, 10, buf);
                CharUtils.getChars(month, 7, buf);
                CharUtils.getChars(year, 4, buf);

            } else {
                if (second == 0 && minute == 0 && hour == 0) {
                    buf = "0000-00-00".toCharArray();
                    CharUtils.getChars(day, 10, buf);
                    CharUtils.getChars(month, 7, buf);
                    CharUtils.getChars(year, 4, buf);
                } else {
                    buf = "0000-00-00T00:00:00".toCharArray();
                    CharUtils.getChars(second, 19, buf);
                    CharUtils.getChars(minute, 16, buf);
                    CharUtils.getChars(hour, 13, buf);
                    CharUtils.getChars(day, 10, buf);
                    CharUtils.getChars(month, 7, buf);
                    CharUtils.getChars(year, 4, buf);
                }
            }

            out.write(buf);

            if (serializer.isEnabled(SerializerFeature.UseSingleQuotes)) {
                out.append('\'');
            } else {
                out.append('\"');
            }
        } else {
            out.writeLong(time);
        }
    }
}
