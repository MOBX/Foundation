package com.lamfire.json.serializer;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLongArray;

public class AtomicLongArraySerializer implements ObjectSerializer {

    public final static AtomicLongArraySerializer instance = new AtomicLongArraySerializer();

    public void write(JSONSerializer serializer, Object object) throws IOException {
        SerializeWriter out = serializer.getWriter();

        if (object == null) {
            if (out.isEnabled(SerializerFeature.WriteNullListAsEmpty)) {
                out.write("[]");
            } else {
                out.writeNull();
            }
            return;
        }

        AtomicLongArray array = (AtomicLongArray) object;
        int len = array.length();
        out.append('[');
        for (int i = 0; i < len; ++i) {
            long val = array.get(i);
            if (i != 0) {
                out.write(',');
            }
            out.writeLong(val);
        }
        out.append(']');
    }
}
