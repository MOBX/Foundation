package com.lamfire.json.serializer;

import java.io.IOException;

public class LongSerializer implements ObjectSerializer {

    public static LongSerializer instance = new LongSerializer();

    public void write(JSONSerializer serializer, Object object) throws IOException {
        SerializeWriter out = serializer.getWriter();

        if (object == null) {
            if (out.isEnabled(SerializerFeature.WriteNullNumberAsZero)) {
                out.write('0');
            } else {
                out.writeNull();
            }
            return;
        }

        Long value = (Long) object;
        out.writeLong(value.longValue());
    }
}
