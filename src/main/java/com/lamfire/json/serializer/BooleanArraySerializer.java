package com.lamfire.json.serializer;

import java.io.IOException;


public class BooleanArraySerializer implements ObjectSerializer {

    public static BooleanArraySerializer instance = new BooleanArraySerializer();

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
        
        out.writeBooleanArray((boolean[]) object);
    }
}
