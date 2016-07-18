package com.lamfire.json.deserializer;

import java.util.ArrayList;

import com.lamfire.json.parser.DefaultExtJSONParser;
import com.lamfire.json.parser.JSONToken;
import com.lamfire.json.parser.ParserConfig;
import com.lamfire.json.util.FieldInfo;

public class ArrayListStringFieldDeserializer extends FieldDeserializer {

    public ArrayListStringFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo) {
        super(clazz, fieldInfo);

    }

    public int getFastMatchToken() {
        return JSONToken.LBRACKET;
    }

    @Override
    public void parseField(DefaultExtJSONParser parser, Object object) {
        ArrayList<Object> list = new ArrayList<Object>();

        ArrayListStringDeserializer.parseArray(parser, list);

        setValue(object, list);
    }
}
