package com.lamfire.json.deserializer;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicLongArray;

import com.lamfire.json.JSONArray;
import com.lamfire.json.parser.DefaultExtJSONParser;
import com.lamfire.json.parser.JSONToken;

public class AtomicLongArrayDeserializer implements ObjectDeserializer {

    public final static AtomicLongArrayDeserializer instance = new AtomicLongArrayDeserializer();

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultExtJSONParser parser, Type clazz) {
        if (parser.getLexer().token() == JSONToken.NULL) {
            parser.getLexer().nextToken(JSONToken.COMMA);
            return null;
        }

        JSONArray array = new JSONArray();
        parser.parseArray(array);

        AtomicLongArray atomicArray = new AtomicLongArray(array.size());
        for (int i = 0; i < array.size(); ++i) {
            atomicArray.set(i, array.getLong(i));
        }

        return (T) atomicArray;
    }

    public int getFastMatchToken() {
        return JSONToken.LBRACKET;
    }
}
