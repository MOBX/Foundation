package com.lamfire.json.serializer;

import java.io.IOException;

public class IntegerSerializer implements ObjectSerializer {

    public static IntegerSerializer instance = new IntegerSerializer();

    public void write(JSONSerializer serializer, Object object) throws IOException {
        SerializeWriter out = serializer.getWriter();

        Number value = (Number) object;

        if (value == null) {
            if (out.isEnabled(SerializerFeature.WriteNullNumberAsZero)) {
                out.write('0');
            } else {
                out.writeNull();
            }
            return;
        }

        out.writeInt(value.intValue());
    }
}
