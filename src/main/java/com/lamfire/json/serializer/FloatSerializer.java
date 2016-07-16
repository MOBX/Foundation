package com.lamfire.json.serializer;

import java.io.IOException;


public class FloatSerializer implements ObjectSerializer {

    public static FloatSerializer instance = new FloatSerializer();

    public void write(JSONSerializer serializer, Object object) throws IOException {
        SerializeWriter out = serializer.getWriter();
        
        if (object == null) {
            if (serializer.isEnabled(SerializerFeature.WriteNullNumberAsZero)) {
                out.write('0');
            } else {
                out.writeNull();                
            }
            return;
        }

        float floatValue = ((Float) object).floatValue(); 
        
        if (Float.isNaN(floatValue)) {
            out.writeNull();
        } else if (Float.isInfinite(floatValue)) {
            out.writeNull();
        } else {
            String floatText= Float.toString(floatValue);
            if (floatText.endsWith(".0")) {
                floatText = floatText.substring(0, floatText.length() - 2);
            }
            out.write(floatText);
        }
    }
}
