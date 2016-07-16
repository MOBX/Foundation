package com.lamfire.json.serializer;

import java.io.IOException;

public class ArraySerializer implements ObjectSerializer {

    private final ObjectSerializer compObjectSerializer;

    public ArraySerializer(ObjectSerializer compObjectSerializer){
        super();
        this.compObjectSerializer = compObjectSerializer;
    }

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

        Object[] array = (Object[]) object;
        int size = array.length;

        int end = size - 1;

        if (end == -1) {
            out.append("[]");
            return;
        }

        out.append('[');
        for (int i = 0; i < end; ++i) {
            Object item = array[i];

            if (item == null) {
                out.append("null,");
            } else {
                compObjectSerializer.write(serializer, item);
                out.append(',');
            }
        }

        Object item = array[end];

        if (item == null) {
            out.append("null]");
        } else {
            compObjectSerializer.write(serializer, item);
            out.append(']');
        }
    }
}
