package com.lamfire.json.deserializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import com.lamfire.json.JSONException;
import com.lamfire.json.parser.DefaultExtJSONParser;
import com.lamfire.json.parser.Feature;
import com.lamfire.json.parser.JSONLexer;
import com.lamfire.json.parser.JSONToken;

@SuppressWarnings({ "unchecked"})
public class ArrayListStringDeserializer implements ObjectDeserializer {

    public final static ArrayListStringDeserializer instance = new ArrayListStringDeserializer();

    
    public <T> T deserialze(DefaultExtJSONParser parser, Type type) {
        ArrayList list = new ArrayList();

        parseArray(parser, list);

        return (T) list;
    }

    public static void parseArray(DefaultExtJSONParser parser, Collection array) {
        JSONLexer lexer = parser.getLexer();

        if (lexer.token() != JSONToken.LBRACKET) {
            throw new JSONException("exepct '[', but " + lexer.token());
        }

        lexer.nextToken(JSONToken.LITERAL_STRING);

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

            String value;
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

            array.add(value);

            if (lexer.token() == JSONToken.COMMA) {
                lexer.nextToken(JSONToken.LITERAL_STRING);
                continue;
            }
        }

        lexer.nextToken(JSONToken.COMMA);
    }

    public int getFastMatchToken() {
        return JSONToken.LBRACKET;
    }
}
