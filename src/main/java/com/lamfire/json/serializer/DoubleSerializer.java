package com.lamfire.json.serializer;

import java.io.IOException;

public class DoubleSerializer implements ObjectSerializer {

    public final static DoubleSerializer instance = new DoubleSerializer();

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

        double doubleValue = ((Double) object).doubleValue(); 
        
        if (Double.isNaN(doubleValue)) {
            out.writeNull();
        } else if (Double.isInfinite(doubleValue)) {
            out.writeNull();
        } else {
            String doubleText = Double.toString(doubleValue);
            if (doubleText.endsWith(".0")) {
                doubleText = doubleText.substring(0, doubleText.length() - 2);
            }
            out.append(doubleText);
        }
    }
}
