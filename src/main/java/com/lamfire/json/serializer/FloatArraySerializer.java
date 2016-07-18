package com.lamfire.json.serializer;

import java.io.IOException;

public class FloatArraySerializer implements ObjectSerializer {

    public static final FloatArraySerializer instance = new FloatArraySerializer();

    public FloatArraySerializer() {
    }

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

        float[] array = (float[]) object;
        int size = array.length;

        int end = size - 1;

        if (end == -1) {
            out.append("[]");
            return;
        }

        out.append('[');
        for (int i = 0; i < end; ++i) {
            float item = array[i];

            if (Float.isNaN(item)) {
                out.writeNull();
            } else {
                out.append(Float.toString(item));
            }

            out.append(',');
        }

        float item = array[end];

        if (Float.isNaN(item)) {
            out.writeNull();
        } else {
            out.append(Float.toString(item));
        }

        out.append(']');
    }
}
