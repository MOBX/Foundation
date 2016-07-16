package com.lamfire.json.deserializer;

import java.lang.reflect.Type;

import com.lamfire.json.parser.DefaultExtJSONParser;
import com.lamfire.json.parser.JSONLexer;
import com.lamfire.json.parser.JSONToken;
import com.lamfire.json.util.TypeConverters;

public class DoubleDeserializer implements ObjectDeserializer {

    public final static DoubleDeserializer instance = new DoubleDeserializer();

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultExtJSONParser parser, Type clazz) {
        return (T) deserialze(parser);
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserialze(DefaultExtJSONParser parser) {
        final JSONLexer lexer = parser.getLexer();
        if (lexer.token() == JSONToken.LITERAL_INT) {
            String val = lexer.numberString();
            lexer.nextToken(JSONToken.COMMA);
            return (T) Double.valueOf(Double.parseDouble(val));
        }

        if (lexer.token() == JSONToken.LITERAL_FLOAT) {
            String val = lexer.numberString();
            lexer.nextToken(JSONToken.COMMA);
            return (T) Double.valueOf(Double.parseDouble(val));
        }

        Object value = parser.parse();

        if (value == null) {
            return null;
        }

        return (T) TypeConverters.castToDouble(value);
    }

    public int getFastMatchToken() {
        return JSONToken.LITERAL_INT;
    }
}
