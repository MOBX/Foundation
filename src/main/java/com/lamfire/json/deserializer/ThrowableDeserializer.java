package com.lamfire.json.deserializer;

import java.lang.reflect.Type;

import com.lamfire.json.parser.DefaultExtJSONParser;
import com.lamfire.json.parser.JSONToken;
import com.lamfire.json.parser.ParserConfig;
import com.lamfire.json.util.TypeConverters;

public class ThrowableDeserializer extends JavaBeanDeserializer {

    public ThrowableDeserializer(ParserConfig mapping, Class<?> clazz) {
        super(mapping, clazz);
    }

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultExtJSONParser parser, Type clazz) {
        Object jsonValue = parser.parse();
        return (T) TypeConverters.cast(jsonValue, clazz, parser.getConfig());
    }

    public int getFastMatchToken() {
        return JSONToken.LBRACE;
    }
}
