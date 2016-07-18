package com.lamfire.json.deserializer;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lamfire.json.parser.DefaultExtJSONParser;
import com.lamfire.json.parser.JSONToken;

public class ConcurrentHashMapDeserializer implements ObjectDeserializer {

    public final static ConcurrentHashMapDeserializer instance = new ConcurrentHashMapDeserializer();

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultExtJSONParser parser, Type clazz) {
        Map<String, Object> map = new ConcurrentHashMap<String, Object>();
        parser.parseObject(map);
        return (T) map;
    }

    public int getFastMatchToken() {
        return JSONToken.LBRACE;
    }
}
