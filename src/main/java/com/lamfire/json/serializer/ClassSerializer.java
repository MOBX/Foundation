package com.lamfire.json.serializer;

import java.io.IOException;

@SuppressWarnings({ "rawtypes" })
public class ClassSerializer implements ObjectSerializer {

    public final static ClassSerializer instance = new ClassSerializer();

    public void write(JSONSerializer serializer, Object object) throws IOException {
        SerializeWriter out = serializer.getWriter();

        Class clazz = (Class) object;
        out.writeString(clazz.getName());
    }
}
