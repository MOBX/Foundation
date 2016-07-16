package com.lamfire.json.serializer;

import java.io.IOException;
import java.math.BigInteger;


public class BigIntegerSerializer implements ObjectSerializer {

    public final static BigIntegerSerializer instance = new BigIntegerSerializer();

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
        
        BigInteger val = (BigInteger) object;
        out.write(val.toString());
    }

}
