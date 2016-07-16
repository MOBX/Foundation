package com.lamfire.json.serializer;

import java.io.IOException;

public class ShortArraySerializer implements ObjectSerializer {

    public static ShortArraySerializer instance = new ShortArraySerializer();

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
        
        short[] array = (short[]) object;
        out.writeShortArray(array);
    }
}
