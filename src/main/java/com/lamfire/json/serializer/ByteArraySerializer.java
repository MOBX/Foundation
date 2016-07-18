package com.lamfire.json.serializer;

import java.io.IOException;

public class ByteArraySerializer implements ObjectSerializer {

    public static ByteArraySerializer instance = new ByteArraySerializer();

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

        byte[] array = (byte[]) object;
        out.writeByteArray(array);
    }
}
