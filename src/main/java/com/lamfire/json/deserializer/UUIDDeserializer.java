package com.lamfire.json.deserializer;

import java.lang.reflect.Type;
import java.util.UUID;

import com.lamfire.json.parser.DefaultExtJSONParser;
import com.lamfire.json.parser.JSONToken;

public class UUIDDeserializer implements ObjectDeserializer {
    public final static UUIDDeserializer instance = new UUIDDeserializer();

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultExtJSONParser parser, Type clazz) {
        
        String name = (String) parser.parse();
        
        if (name == null) {
            return null;
        }
        
        return (T) UUID.fromString(name);
    }

    public int getFastMatchToken() {
        return JSONToken.LITERAL_STRING;
    }
}
