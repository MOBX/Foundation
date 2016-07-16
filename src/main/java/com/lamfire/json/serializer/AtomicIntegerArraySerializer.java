package com.lamfire.json.serializer;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicIntegerArray;


public class AtomicIntegerArraySerializer implements ObjectSerializer {

    public final static AtomicIntegerArraySerializer instance = new AtomicIntegerArraySerializer();

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

        AtomicIntegerArray array = (AtomicIntegerArray) object;
        int len = array.length();
        out.append('[');
        for (int i = 0; i < len; ++i) {
            int val = array.get(i);
            if (i != 0) {
                out.write(',');
            }
            out.writeInt(val);
        }
        out.append(']');
    }

}
