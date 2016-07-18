package com.lamfire.json.serializer;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicLongSerializer implements ObjectSerializer {

    public final static AtomicLongSerializer instance = new AtomicLongSerializer();

    public void write(JSONSerializer serializer, Object object) throws IOException {
        SerializeWriter out = serializer.getWriter();

        AtomicLong val = (AtomicLong) object;
        out.writeLong(val.get());
    }
}
