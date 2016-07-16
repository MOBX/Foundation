package com.lamfire.json.serializer;

import java.io.IOException;

import com.lamfire.json.JSONString;


public class JSONAwareSerializer implements ObjectSerializer {

    public static JSONAwareSerializer instance = new JSONAwareSerializer();

    public void write(JSONSerializer serializer, Object object) throws IOException {
        SerializeWriter out = serializer.getWriter();

        JSONString aware = (JSONString) object;
        out.write(aware.toJSONString());
    }
}
