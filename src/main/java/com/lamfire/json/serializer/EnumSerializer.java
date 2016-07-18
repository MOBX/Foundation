package com.lamfire.json.serializer;

import java.io.IOException;

public class EnumSerializer implements ObjectSerializer {

    public final static EnumSerializer instance = new EnumSerializer();

    public void write(JSONSerializer serializer, Object object) throws IOException {
        SerializeWriter out = serializer.getWriter();

        if (serializer.isEnabled(SerializerFeature.WriteEnumUsingToString)) {
            Enum<?> e = (Enum<?>) object;
            serializer.write(e.name());
        } else {
            Enum<?> e = (Enum<?>) object;
            out.writeInt(e.ordinal());
        }
    }
}
