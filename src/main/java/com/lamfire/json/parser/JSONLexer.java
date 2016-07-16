package com.lamfire.json.parser;

import java.math.BigDecimal;
import java.util.Calendar;

import com.lamfire.json.util.SymbolTable;

public interface JSONLexer {

    void nextToken();

    void nextToken(int expect);

    int token();

    int pos();

    String stringVal();

    Number integerValue();
    
    void nextTokenWithColon(int expect);

    BigDecimal decimalValue();
    
    double doubleValue();
    
    float floatValue();

    void config(Feature feature, boolean state);

    boolean isEnabled(Feature feature);

    String numberString();

    boolean isEOF();

    String symbol(SymbolTable symbolTable);

    boolean isBlankInput();

    char getCurrent();

    void skipWhitespace();

    void incrementBufferPosition();

    String scanSymbol(final SymbolTable symbolTable);

    String scanSymbol(final SymbolTable symbolTable, final char quote);

    void resetStringPosition();

    String scanSymbolUnQuoted(final SymbolTable symbolTable);

    void scanString();

    void scanNumber();

    boolean scanISO8601DateIfMatch();

    Calendar getCalendar();

    int intValue() throws NumberFormatException;

    long longValue() throws NumberFormatException;
}
