package com.lamfire.json.serializer;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings({ "unchecked"})
public class AtomicReferenceSerializer implements ObjectSerializer {

    public final static AtomicReferenceSerializer instance = new AtomicReferenceSerializer();

 
    public void write(JSONSerializer serializer, Object object) throws IOException {
        AtomicReference val = (AtomicReference) object;
        serializer.write(val.get());
    }

}
