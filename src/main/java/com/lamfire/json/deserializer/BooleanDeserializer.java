package com.lamfire.json.deserializer;

import java.lang.reflect.Type;

import com.lamfire.json.parser.DefaultExtJSONParser;
import com.lamfire.json.parser.JSONLexer;
import com.lamfire.json.parser.JSONToken;
import com.lamfire.json.util.TypeConverters;

public class BooleanDeserializer implements ObjectDeserializer {
    public final static BooleanDeserializer instance = new BooleanDeserializer();
    
    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultExtJSONParser parser, Type clazz) {
        return (T) deserialze(parser);
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserialze(DefaultExtJSONParser parser) {
        final JSONLexer lexer = parser.getLexer();
        
        if (lexer.token() == JSONToken.TRUE) {
            lexer.nextToken(JSONToken.COMMA);
            return (T) Boolean.TRUE;
        }
        
        if (lexer.token() == JSONToken.FALSE) {
            lexer.nextToken(JSONToken.COMMA);
            return (T) Boolean.FALSE;
        }
        
        if (lexer.token() == JSONToken.LITERAL_INT) {
            int intValue = lexer.intValue();
            lexer.nextToken(JSONToken.COMMA);
            
            if (intValue == 1) {
                return (T) Boolean.TRUE;
            } else {
                return (T) Boolean.FALSE;
            }
        }
        
        Object value = parser.parse();

        if (value == null) {
            return null;
        }
        
        return (T) TypeConverters.castToBoolean(value);
    }

    public int getFastMatchToken() {
        return JSONToken.TRUE;
    }
}
