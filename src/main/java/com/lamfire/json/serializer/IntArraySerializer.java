package com.lamfire.json.serializer;

import java.io.IOException;


public class IntArraySerializer implements ObjectSerializer {

    public static IntArraySerializer instance = new IntArraySerializer();

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

        int[] array = (int[]) object;

        out.writeIntArray(array);
    }
}
