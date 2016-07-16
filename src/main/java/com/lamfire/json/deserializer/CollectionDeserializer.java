package com.lamfire.json.deserializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.lamfire.json.JSONException;
import com.lamfire.json.parser.DefaultExtJSONParser;
import com.lamfire.json.parser.JSONToken;

@SuppressWarnings({ "unchecked"})
public class CollectionDeserializer implements ObjectDeserializer {

    public final static CollectionDeserializer instance = new CollectionDeserializer();

 
    public <T> T deserialze(DefaultExtJSONParser parser, Type type) {
        if (parser.getLexer().token() == JSONToken.NULL) {
            parser.getLexer().nextToken(JSONToken.COMMA);
            return null;
        }

        Collection list = null;
        if (type instanceof Class<?>) {
            Class<?> clazz = (Class<?>) type;
            if (clazz.isAssignableFrom(HashSet.class)) {
                list = new HashSet();
            } else if (clazz.isAssignableFrom(ArrayList.class)) {
                list = new ArrayList();
            } else {
                try {
                    list = (Collection) clazz.newInstance();
                } catch (Exception e) {
                    throw new JSONException("create instane error, class " + clazz.getName());
                }
            }
        }
        
        if (list == null) {
            list = new ArrayList();
        }

        Type itemType;
        if (type instanceof ParameterizedType) {
            itemType = ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            itemType = Object.class;
        }
        parser.parseArray(itemType, list);

        return (T) list;
    }

    public int getFastMatchToken() {
        return JSONToken.LBRACKET;
    }
}
