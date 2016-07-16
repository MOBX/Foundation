package com.lamfire.json.deserializer;

import java.lang.reflect.Type;

import com.lamfire.json.parser.DefaultExtJSONParser;
import com.lamfire.json.parser.JSONToken;
import com.lamfire.json.util.TypeConverters;

public class ByteDeserializer implements ObjectDeserializer {
    public final static ByteDeserializer instance = new ByteDeserializer();

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultExtJSONParser parser, Type clazz) {
        Object value = parser.parse();

        if (value == null) {
            return null;
        }
        
        return (T) TypeConverters.castToByte(value);
    }

    public int getFastMatchToken() {
        return JSONToken.LITERAL_INT;
    }
}
