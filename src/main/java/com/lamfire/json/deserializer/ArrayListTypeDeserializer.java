package com.lamfire.json.deserializer;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.lamfire.json.parser.DefaultExtJSONParser;
import com.lamfire.json.parser.JSONToken;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ArrayListTypeDeserializer implements ObjectDeserializer {

    private Type itemType;

    public ArrayListTypeDeserializer(Type type) {
        this.itemType = type;
    }

    public <T> T deserialze(DefaultExtJSONParser parser, Type type) {
        ArrayList list = new ArrayList();

        parser.parseArray(itemType, list);

        return (T) list;
    }

    public int getFastMatchToken() {
        return JSONToken.LBRACKET;
    }
}
