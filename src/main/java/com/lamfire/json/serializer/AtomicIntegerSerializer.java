package com.lamfire.json.serializer;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerSerializer implements ObjectSerializer {

    public final static AtomicIntegerSerializer instance = new AtomicIntegerSerializer();

    public void write(JSONSerializer serializer, Object object) throws IOException {
        SerializeWriter out = serializer.getWriter();

        AtomicInteger val = (AtomicInteger) object;
        out.writeInt(val.get());
    }
}
