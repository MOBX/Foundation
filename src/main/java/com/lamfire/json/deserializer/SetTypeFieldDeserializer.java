package com.lamfire.json.deserializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import com.lamfire.json.JSONException;
import com.lamfire.json.parser.*;
import com.lamfire.json.util.FieldInfo;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class SetTypeFieldDeserializer extends FieldDeserializer {

    private final Type         itemType;
    private int                itemFastMatchToken;
    private ObjectDeserializer deserializer;

    public SetTypeFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo) {
        super(clazz, fieldInfo);

        this.itemType = ((ParameterizedType) getFieldType()).getActualTypeArguments()[0];

    }

    public int getFastMatchToken() {
        return JSONToken.LBRACKET;
    }

    @Override
    public void parseField(DefaultExtJSONParser parser, Object object) {
        if (parser.getLexer().token() == JSONToken.NULL) {
            setValue(object, null);
            return;
        }

        Set set = null;
        if (super.getFieldClass() == TreeSet.class) {
            set = new TreeSet();
        } else {
            set = new HashSet();
        }
        parseArray(parser, set);

        setValue(object, set);
    }

    public final void parseArray(DefaultExtJSONParser parser, Collection array) {
        final JSONLexer lexer = parser.getLexer();

        if (lexer.token() != JSONToken.LBRACKET) {
            throw new JSONException("exepct '[', but " + JSONToken.name(lexer.token()));
        }

        if (deserializer == null) {
            deserializer = parser.getConfig().getDeserializer(itemType);
            itemFastMatchToken = deserializer.getFastMatchToken();
        }

        lexer.nextToken(itemFastMatchToken);

        for (;;) {
            if (lexer.isEnabled(Feature.AllowArbitraryCommas)) {
                while (lexer.token() == JSONToken.COMMA) {
                    lexer.nextToken();
                    continue;
                }
            }

            if (lexer.token() == JSONToken.RBRACKET) {
                break;
            }

            Object val = deserializer.deserialze(parser, itemType);
            array.add(val);

            if (lexer.token() == JSONToken.COMMA) {
                lexer.nextToken(itemFastMatchToken);
                continue;
            }
        }

        lexer.nextToken(JSONToken.COMMA);
    }
}
