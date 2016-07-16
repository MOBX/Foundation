package com.lamfire.json.serializer;

public interface ValueFilter {

    Object process(Object source, String name, Object value);
}
