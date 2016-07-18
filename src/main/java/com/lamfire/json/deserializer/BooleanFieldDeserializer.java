package com.lamfire.json.deserializer;

import com.lamfire.json.parser.*;
import com.lamfire.json.util.FieldInfo;
import com.lamfire.json.util.TypeConverters;

public class BooleanFieldDeserializer extends FieldDeserializer {

    public BooleanFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo) {
        super(clazz, fieldInfo);
    }

    @Override
    public void parseField(DefaultExtJSONParser parser, Object object) {
        Boolean value;

        final JSONLexer lexer = parser.getLexer();
        if (lexer.token() == JSONToken.TRUE) {
            lexer.nextToken(JSONToken.COMMA);
            setValue(object, true);
            return;
        }

        if (lexer.token() == JSONToken.LITERAL_INT) {
            int val = lexer.intValue();
            lexer.nextToken(JSONToken.COMMA);
            if (val == 1) {
                setValue(object, true);
            } else {
                setValue(object, false);
            }
            return;
        }

        if (lexer.token() == JSONToken.NULL) {
            value = null;
            lexer.nextToken(JSONToken.COMMA);

            if (getFieldClass() == boolean.class) {
                // skip
                return;
            }

            setValue(object, null);
            return;
        }

        if (lexer.token() == JSONToken.FALSE) {
            lexer.nextToken(JSONToken.COMMA);
            setValue(object, false);
            return;
        }

        Object obj = parser.parse();

        value = TypeConverters.castToBoolean(obj);

        if (value == null && getFieldClass() == boolean.class) {
            // skip
            return;
        }

        setValue(object, value);
    }

    public int getFastMatchToken() {
        return JSONToken.TRUE;
    }
}
