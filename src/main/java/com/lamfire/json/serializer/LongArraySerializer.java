package com.lamfire.json.serializer;

import java.io.IOException;

public class LongArraySerializer implements ObjectSerializer {

    public static LongArraySerializer instance = new LongArraySerializer();

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

        long[] array = (long[]) object;

        out.writeLongArray(array);
    }
    

}
