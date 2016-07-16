package com.lamfire.json.serializer;

import java.io.IOException;


public class BooleanSerializer implements ObjectSerializer {

    public final static BooleanSerializer instance = new BooleanSerializer();

    public void write(JSONSerializer serializer, Object object) throws IOException {
        SerializeWriter out = serializer.getWriter();

        Boolean value = (Boolean) object;
        if (value == null) {
            if (out.isEnabled(SerializerFeature.WriteNullBooleanAsFalse)) {
                out.write("false");
            } else {
                out.writeNull();
            }
            return;
        }

        if (value.booleanValue()) {
            out.write("true");
        } else {
            out.write("false");
        }
    }

}
