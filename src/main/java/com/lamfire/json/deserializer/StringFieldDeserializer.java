package com.lamfire.json.deserializer;

import com.lamfire.json.parser.*;
import com.lamfire.json.util.FieldInfo;

public class StringFieldDeserializer extends FieldDeserializer {

    private final ObjectDeserializer fieldValueDeserilizer;

    public StringFieldDeserializer(ParserConfig config, Class<?> clazz, FieldInfo fieldInfo) {
        super(clazz, fieldInfo);

        fieldValueDeserilizer = config.getDeserializer(fieldInfo);
    }

    @Override
    public void parseField(DefaultExtJSONParser parser, Object object) {
        String value;

        final JSONLexer lexer = parser.getLexer();
        if (lexer.token() == JSONToken.LITERAL_STRING) {
            value = lexer.stringVal();
            lexer.nextToken(JSONToken.COMMA);
        } else {

            Object obj = parser.parse();

            if (obj == null) {
                value = null;
            } else {
                value = obj.toString();
            }
        }

        setValue(object, value);
    }

    public int getFastMatchToken() {
        return fieldValueDeserilizer.getFastMatchToken();
    }
}
