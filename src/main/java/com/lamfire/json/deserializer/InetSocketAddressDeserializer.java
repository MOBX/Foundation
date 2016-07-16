package com.lamfire.json.deserializer;

import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import com.lamfire.json.JSONException;
import com.lamfire.json.parser.DefaultExtJSONParser;
import com.lamfire.json.parser.JSONLexer;
import com.lamfire.json.parser.JSONToken;

public class InetSocketAddressDeserializer implements ObjectDeserializer {

    public final static InetSocketAddressDeserializer instance = new InetSocketAddressDeserializer();

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultExtJSONParser parser, Type clazz) {
        JSONLexer lexer = parser.getLexer();

        if (lexer.token() == JSONToken.NULL) {
            lexer.nextToken();
            return null;
        }

        parser.accept(JSONToken.LBRACE);

        InetAddress address = null;
        int port = 0;
        for (;;) {
            String key = lexer.symbol(parser.getSymbolTable());
            lexer.nextToken();

            if (key.equals("address")) {
                parser.accept(JSONToken.COLON);
                address = parser.parseObject(InetAddress.class);
            } else if (key.equals("port")) {
                parser.accept(JSONToken.COLON);
                if (lexer.token() != JSONToken.LITERAL_INT) {
                    throw new JSONException("port is not int");
                }
                port = lexer.intValue();
                lexer.nextToken();
            } else {
                parser.accept(JSONToken.COLON);
                parser.parse();
            }

            if (lexer.token() == JSONToken.COMMA) {
                lexer.nextToken();
                continue;
            }

            break;
        }

        parser.accept(JSONToken.RBRACE);

        return (T) new InetSocketAddress(address, port);
    }

    public int getFastMatchToken() {
        return JSONToken.LBRACE;
    }
}
