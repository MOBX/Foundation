package com.lamfire.json.deserializer;

import java.lang.reflect.Type;

import com.lamfire.json.parser.DefaultExtJSONParser;
import com.lamfire.json.parser.JSONToken;
import com.lamfire.json.util.TypeConverters;

public class ShortDeserializer implements ObjectDeserializer {
    public final static ShortDeserializer instance = new ShortDeserializer();

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultExtJSONParser parser, Type clazz) {
        Object value = parser.parse();

        if (value == null) {
            return null;
        }
        
        return (T) TypeConverters.castToShort(value);
    }

    public int getFastMatchToken() {
        return JSONToken.LITERAL_INT;
    }
}
