package com.lamfire.json.deserializer;

import com.lamfire.json.parser.*;
import com.lamfire.json.util.FieldInfo;
import com.lamfire.json.util.TypeConverters;

public class LongFieldDeserializer extends FieldDeserializer {

    private final ObjectDeserializer fieldValueDeserilizer;

    public LongFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo) {
        super(clazz, fieldInfo);

        fieldValueDeserilizer = mapping.getDeserializer(fieldInfo);
    }

    @Override
    public void parseField(DefaultExtJSONParser parser, Object object) {
        Long value;

        final JSONLexer lexer = parser.getLexer();
        if (lexer.token() == JSONToken.LITERAL_INT) {
            long val = lexer.longValue();
            lexer.nextToken(JSONToken.COMMA);
            setValue(object, val);
            return;
        } else if (lexer.token() == JSONToken.NULL) {
            value = null;
            lexer.nextToken(JSONToken.COMMA);

        } else {
            Object obj = parser.parse();

            value = TypeConverters.castToLong(obj);
        }

        if (value == null && getFieldClass() == long.class) {
            // skip
            return;
        }

        setValue(object, value);
    }

    public int getFastMatchToken() {
        return fieldValueDeserilizer.getFastMatchToken();
    }
}
