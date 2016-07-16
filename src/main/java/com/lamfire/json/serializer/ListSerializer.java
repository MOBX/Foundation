package com.lamfire.json.serializer;

import java.io.IOException;
import java.util.List;


public final class ListSerializer implements ObjectSerializer {

    public static final ListSerializer instance = new ListSerializer();

    public final void write(JSONSerializer serializer, Object object) throws IOException {
        SerializeWriter out = serializer.getWriter();

        if (object == null) {
            if (out.isEnabled(SerializerFeature.WriteNullListAsEmpty)) {
                out.write("[]");
            } else {
                out.writeNull();
            }
            return;
        }

        List<?> list = (List<?>) object;

        final int size = list.size();
        int end = size - 1;

        if (end == -1) {
            out.append("[]");
            return;
        }

        out.append('[');
        for (int i = 0; i < end; ++i) {
            Object item = list.get(i);

            if (item == null) {
                out.append("null,");
            } else {
                Class<?> clazz = item.getClass();

                if (clazz == Integer.class) {
                    out.writeIntAndChar(((Integer) item).intValue(), ',');
                } else if (clazz == Long.class) {
                    long val = ((Long) item).longValue();
                    out.writeLongAndChar(val, ',');
                } else {
                    serializer.write(item);
                    out.append(',');
                }
            }
        }

        Object item = list.get(end);

        if (item == null) {
            out.append("null]");
        } else {
            Class<?> clazz = item.getClass();

            if (clazz == Integer.class) {
                out.writeIntAndChar(((Integer) item).intValue(), ']');
            } else if (clazz == Long.class) {
                out.writeLongAndChar(((Long) item).longValue(), ']');
            } else {
                serializer.write(item);
                out.append(']');
            }
        }
    }
}
