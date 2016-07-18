package com.lamfire.json.deserializer;

import com.lamfire.json.parser.*;
import com.lamfire.json.util.FieldInfo;
import com.lamfire.json.util.TypeConverters;

public class IntegerFieldDeserializer extends FieldDeserializer {

    public IntegerFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo) {
        super(clazz, fieldInfo);
    }

    @Override
    public void parseField(DefaultExtJSONParser parser, Object object) {
        Integer value;

        final JSONLexer lexer = parser.getLexer();
        if (lexer.token() == JSONToken.LITERAL_INT) {
            int val = lexer.intValue();
            lexer.nextToken(JSONToken.COMMA);
            setValue(object, val);
            return;
        } else if (lexer.token() == JSONToken.NULL) {
            value = null;
            lexer.nextToken(JSONToken.COMMA);
        } else {
            Object obj = parser.parse();

            value = TypeConverters.castToInt(obj);
        }

        if (value == null && getFieldClass() == int.class) {
            // skip
            return;
        }

        setValue(object, value);
    }

    public int getFastMatchToken() {
        return JSONToken.LITERAL_INT;
    }
}
