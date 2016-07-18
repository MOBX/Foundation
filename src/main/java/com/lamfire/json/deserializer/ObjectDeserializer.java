package com.lamfire.json.deserializer;

import java.lang.reflect.Type;

import com.lamfire.json.parser.DefaultExtJSONParser;

public interface ObjectDeserializer {

    <T> T deserialze(DefaultExtJSONParser parser, Type type);

    int getFastMatchToken();
}
