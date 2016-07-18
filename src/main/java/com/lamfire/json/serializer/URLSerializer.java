package com.lamfire.json.serializer;

import java.io.IOException;

public class URLSerializer implements ObjectSerializer {

    public final static URLSerializer instance = new URLSerializer();

    public void write(JSONSerializer serializer, Object object) throws IOException {
        if (object == null) {
            serializer.writeNull();
            return;
        }

        serializer.write(object.toString());
    }
}
