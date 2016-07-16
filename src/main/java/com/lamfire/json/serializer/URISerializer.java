package com.lamfire.json.serializer;

import java.io.IOException;
import java.net.URI;


public class URISerializer implements ObjectSerializer {

    public final static URISerializer instance = new URISerializer();

    public void write(JSONSerializer serializer, Object object) throws IOException {
        if (object == null) {
            serializer.writeNull();
            return;
        }

        URI uri = (URI) object;
        serializer.write(uri.toString());
    }

}
