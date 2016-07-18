package com.lamfire.json.deserializer;

import java.lang.reflect.Type;
import java.util.Date;

import com.lamfire.json.JSONException;
import com.lamfire.json.parser.DefaultExtJSONParser;
import com.lamfire.json.parser.JSONToken;

public class SqlDateDeserializer implements ObjectDeserializer {

    public final static SqlDateDeserializer instance = new SqlDateDeserializer();

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultExtJSONParser parser, Type clazz) {
        Object val = parser.parse();
        if (val == null) {
            return null;
        }

        if (val instanceof Date) {
            val = new java.sql.Date(((Date) val).getTime());
        } else if (val instanceof Number) {
            val = (T) new java.sql.Date(((Number) val).longValue());
        } else if (val instanceof String) {
            String strVal = (String) val;
            if (strVal.length() == 0) {
                return null;
            }

            long longVal = Long.parseLong(strVal);
            return (T) new java.sql.Date(longVal);
        } else {
            throw new JSONException("parse error : " + val);
        }

        return (T) val;
    }

    public int getFastMatchToken() {
        return JSONToken.LITERAL_INT;
    }
}
