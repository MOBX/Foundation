package com.lamfire.json.serializer;

import java.io.IOException;

public class StringSerializer implements ObjectSerializer {

    public static StringSerializer instance = new StringSerializer();

    public void write(JSONSerializer serializer, Object object) throws IOException {
        write(serializer, (String) object);
    }

    public void write(JSONSerializer serializer, String value) {
        SerializeWriter out = serializer.getWriter();

        if (value == null) {
            if (out.isEnabled(SerializerFeature.WriteNullStringAsEmpty)) {
                out.writeString("");
            } else {
                out.writeNull();
            }
            return;
        }

        out.writeString(value);
    }
}
