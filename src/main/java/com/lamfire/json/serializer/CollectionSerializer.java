package com.lamfire.json.serializer;

import java.io.IOException;
import java.util.Collection;


public class CollectionSerializer implements ObjectSerializer {

    public final static CollectionSerializer instance = new CollectionSerializer();

    public void write(JSONSerializer serializer, Object object) throws IOException {
        SerializeWriter out = serializer.getWriter();
        
        if (object == null) {
            if (out.isEnabled(SerializerFeature.WriteNullListAsEmpty)) {
                out.write("[]");
            } else {
                out.writeNull();
            }
            return;
        }

        Collection<?> collection = (Collection<?>) object;
        
        out.append('[');
        boolean first = true;
        for (Object item : collection) {
            if (!first) {
                out.append(',');
            }
            first = false;

            if (item == null) {
                out.writeNull();
                continue;
            }

            Class<?> clazz = item.getClass();

            if (clazz == Integer.class) {
                out.writeInt(((Integer) item).intValue());
                continue;
            }

            if (clazz == Long.class) {
                out.writeLong(((Long) item).longValue());
                continue;
            }

            serializer.write(item);
        }
        out.append(']');
    }

}
