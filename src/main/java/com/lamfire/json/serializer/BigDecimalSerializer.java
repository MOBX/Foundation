package com.lamfire.json.serializer;

import java.io.IOException;
import java.math.BigDecimal;


public class BigDecimalSerializer implements ObjectSerializer {

    public final static BigDecimalSerializer instance = new BigDecimalSerializer();

    public void write(JSONSerializer serializer, Object object) throws IOException {
        SerializeWriter out = serializer.getWriter();

        if (object == null) {
            if (out.isEnabled(SerializerFeature.WriteNullNumberAsZero)) {
                out.write('0');
            } else {
                out.writeNull();
            }
            return;
        }

        BigDecimal val = (BigDecimal) object;
        out.write(val.toString());
    }

}
