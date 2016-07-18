package com.lamfire.json.parser;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import com.lamfire.json.JSON;
import com.lamfire.json.JSONArray;
import com.lamfire.json.JSONException;

@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class AbstractJSONParser {

    public abstract void parseObject(final Map object);

    public JSON parseObject() {
        JSON object = new JSON();
        parseObject(object);
        return object;
    }

    public final void parseArray(final Collection array) {
        final JSONLexer lexer = getLexer();

        if (lexer.token() != JSONToken.LBRACKET) {
            throw new JSONException("syntax error, expect [, actual " + JSONToken.name(lexer.token()));
        }

        lexer.nextToken(JSONToken.LITERAL_STRING);

        for (;;) {
            if (isEnabled(Feature.AllowArbitraryCommas)) {
                while (lexer.token() == JSONToken.COMMA) {
                    lexer.nextToken();
                    continue;
                }
            }

            Object value;
            switch (lexer.token()) {
                case JSONToken.LITERAL_INT:
                    value = lexer.integerValue();
                    lexer.nextToken(JSONToken.COMMA);
                    break;
                case JSONToken.LITERAL_FLOAT:
                    if (lexer.isEnabled(Feature.UseBigDecimal)) {
                        value = lexer.decimalValue();
                    } else {
                        value = lexer.doubleValue();
                    }
                    lexer.nextToken(JSONToken.COMMA);
                    break;
                case JSONToken.LITERAL_STRING:
                    String stringLiteral = lexer.stringVal();
                    lexer.nextToken(JSONToken.COMMA);

                    if (lexer.isEnabled(Feature.AllowISO8601DateFormat)) {
                        JSONScanner iso8601Lexer = new JSONScanner(stringLiteral);
                        if (iso8601Lexer.scanISO8601DateIfMatch()) {
                            value = iso8601Lexer.getCalendar().getTime();
                        } else {
                            value = stringLiteral;
                        }
                    } else {
                        value = stringLiteral;
                    }

                    break;
                case JSONToken.TRUE:
                    value = Boolean.TRUE;
                    lexer.nextToken(JSONToken.COMMA);
                    break;
                case JSONToken.FALSE:
                    value = Boolean.FALSE;
                    lexer.nextToken(JSONToken.COMMA);
                    break;
                case JSONToken.LBRACE:
                    JSON object = new JSON();
                    parseObject(object);
                    value = object;
                    break;
                case JSONToken.LBRACKET:
                    Collection items = new JSONArray();
                    parseArray(items);
                    value = items;
                    break;
                case JSONToken.NULL:
                    value = null;
                    lexer.nextToken(JSONToken.LITERAL_STRING);
                    break;
                case JSONToken.RBRACKET:
                    lexer.nextToken(JSONToken.COMMA);
                    return;
                default:
                    value = parse();
                    break;
            }

            array.add(value);

            if (lexer.token() == JSONToken.COMMA) {
                lexer.nextToken(JSONToken.LITERAL_STRING);
                continue;
            }
        }
    }

    public Object parse() {
        final JSONLexer lexer = getLexer();
        switch (lexer.token()) {
            case JSONToken.LBRACKET:
                JSONArray array = new JSONArray();
                parseArray(array);
                return array;
            case JSONToken.LBRACE:
                JSON object = new JSON();
                parseObject(object);
                return object;
            case JSONToken.LITERAL_INT:
                Number intValue = lexer.integerValue();
                lexer.nextToken();
                return intValue;
            case JSONToken.LITERAL_FLOAT:

                Object value;
                if (isEnabled(Feature.UseBigDecimal)) {
                    value = lexer.decimalValue();
                } else {
                    value = lexer.doubleValue();
                }
                lexer.nextToken();
                return value;
            case JSONToken.LITERAL_STRING:
                String stringLiteral = lexer.stringVal();
                lexer.nextToken(JSONToken.COMMA);

                if (lexer.isEnabled(Feature.AllowISO8601DateFormat)) {
                    JSONScanner iso8601Lexer = new JSONScanner(stringLiteral);
                    if (iso8601Lexer.scanISO8601DateIfMatch()) {
                        return iso8601Lexer.getCalendar().getTime();
                    }
                }

                return stringLiteral;
            case JSONToken.NULL:
                lexer.nextToken();
                return null;
            case JSONToken.TRUE:
                lexer.nextToken();
                return Boolean.TRUE;
            case JSONToken.FALSE:
                lexer.nextToken();
                return Boolean.FALSE;
            case JSONToken.NEW:
                lexer.nextToken(JSONToken.IDENTIFIER);

                if (lexer.token() != JSONToken.IDENTIFIER) {
                    throw new JSONException("syntax error");
                }
                lexer.nextToken(JSONToken.LPAREN);

                accept(JSONToken.LPAREN);
                long time = ((Number) lexer.integerValue()).longValue();
                accept(JSONToken.LITERAL_INT);

                accept(JSONToken.RPAREN);

                return new Date(time);
            case JSONToken.EOF:
                if (lexer.isBlankInput()) {
                    return null;
                }
            default:
                throw new JSONException("TODO " + JSONToken.name(lexer.token()) + " " + lexer.stringVal());
        }
    }

    public void config(Feature feature, boolean state) {
        getLexer().config(feature, state);
    }

    public boolean isEnabled(Feature feature) {
        return getLexer().isEnabled(feature);
    }

    public abstract JSONLexer getLexer();

    public final void accept(final int token) {
        final JSONLexer lexer = getLexer();
        if (lexer.token() == token) {
            lexer.nextToken();
        } else {
            throw new JSONException("syntax error, expect " + JSONToken.name(token) + ", actual "
                                    + JSONToken.name(lexer.token()));
        }
    }

    public void close() {
        final JSONLexer lexer = getLexer();

        if (isEnabled(Feature.AutoCloseSource)) {
            if (!lexer.isEOF()) {
                throw new JSONException("not close json text, token : " + JSONToken.name(lexer.token()));
            }
        }
    }
}
