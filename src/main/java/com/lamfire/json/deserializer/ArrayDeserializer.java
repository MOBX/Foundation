package com.lamfire.json.deserializer;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

import com.lamfire.json.JSONArray;
import com.lamfire.json.parser.DefaultExtJSONParser;
import com.lamfire.json.parser.JSONToken;
import com.lamfire.json.util.TypeConverters;

public class ArrayDeserializer implements ObjectDeserializer {

    public final static ArrayDeserializer instance = new ArrayDeserializer();

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultExtJSONParser parser, Type clazz) {
        if (parser.getLexer().token() == JSONToken.NULL) {
            parser.getLexer().nextToken(JSONToken.COMMA);
            return null;
        }

        JSONArray array = new JSONArray();
        parser.parseArray(array);
        
        if(clazz instanceof GenericArrayType){
			Type componentType = ((GenericArrayType) clazz).getGenericComponentType();
        	return toObjectArray(parser, (Class<T>) componentType, array);
		}

        Class<T> arrayType = ((Class<T>) clazz);
        
        Type componentType = null;
        if(arrayType == null){
        	componentType = (Type)byte.class;
        }else{
        	componentType = arrayType.getComponentType();
        }
        return toObjectArray(parser, (Class<T>)componentType, array);
    }
    
    @SuppressWarnings("unchecked")
    private <T> T toObjectArray(DefaultExtJSONParser parser, Class<T> componentType, JSONArray array) {
        int size = array.size();
        Object objArray = Array.newInstance(componentType, size);
        for (int i = 0; i < size; ++i) {
            Object value = array.get(i);

            if (componentType.isArray()) {
                Object element = toObjectArray(parser, componentType, (JSONArray) value);
                Array.set(objArray, i, element);
            } else {
                Object element = TypeConverters.cast(value, componentType, parser.getConfig());
                Array.set(objArray, i, element);
            }
        }
        return (T) objArray; // TODO
    }

    public int getFastMatchToken() {
        return JSONToken.LBRACKET;
    }
}
