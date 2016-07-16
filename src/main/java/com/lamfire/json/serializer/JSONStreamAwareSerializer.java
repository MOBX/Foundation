package com.lamfire.json.serializer;

import java.io.IOException;

import com.lamfire.json.JSONWriter;

public class JSONStreamAwareSerializer implements ObjectSerializer {

    public static JSONStreamAwareSerializer instance = new JSONStreamAwareSerializer();

    public void write(JSONSerializer serializer, Object object) throws IOException {
        SerializeWriter out = serializer.getWriter();

        JSONWriter aware = (JSONWriter) object;
        aware.write(out);
    }
}
