package com.lamfire.json.deserializer;

import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.lamfire.json.JSONException;
import com.lamfire.json.parser.DefaultExtJSONParser;
import com.lamfire.json.parser.JSONToken;

public class InetAddressDeserializer implements ObjectDeserializer {

    public final static InetAddressDeserializer instance = new InetAddressDeserializer();

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultExtJSONParser parser, Type clazz) {

        String host = (String) parser.parse();

        if (host == null) {
            return null;
        }

        if (host.length() == 0) {
            return null;
        }

        try {
            return (T) InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            throw new JSONException("deserialize error", e);
        }
    }

    public int getFastMatchToken() {
        return JSONToken.LITERAL_STRING;
    }

}
