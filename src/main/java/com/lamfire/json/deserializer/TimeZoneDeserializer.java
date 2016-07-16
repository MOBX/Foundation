package com.lamfire.json.deserializer;

import java.lang.reflect.Type;
import java.util.TimeZone;

import com.lamfire.json.parser.DefaultExtJSONParser;
import com.lamfire.json.parser.JSONToken;

public class TimeZoneDeserializer implements ObjectDeserializer {
    public final static TimeZoneDeserializer instance = new TimeZoneDeserializer();

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultExtJSONParser parser, Type clazz) {
        
        String id = (String) parser.parse();
        
        if (id == null) {
            return null;
        }
        
        return (T) TimeZone.getTimeZone(id);
    }

    public int getFastMatchToken() {
        return JSONToken.LITERAL_STRING;
    }
}
