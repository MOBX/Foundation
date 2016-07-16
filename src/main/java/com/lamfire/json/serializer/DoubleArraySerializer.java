package com.lamfire.json.serializer;

import java.io.IOException;


public class DoubleArraySerializer implements ObjectSerializer {

    public static final DoubleArraySerializer instance = new DoubleArraySerializer();

    public DoubleArraySerializer(){
    }

    public final void write(JSONSerializer serializer, Object object) throws IOException {
        SerializeWriter out = serializer.getWriter();
        
        if (object == null) {
            if (out.isEnabled(SerializerFeature.WriteNullListAsEmpty)) {
                out.write("[]");
            } else {
                out.writeNull();
            }
            return;
        }

        double[] array = (double[]) object;
        int size = array.length;

        int end = size - 1;

        if (end == -1) {
            out.append("[]");
            return;
        }

        out.append('[');
        for (int i = 0; i < end; ++i) {
            double item = array[i];

            if (Double.isNaN(item)) {
                out.writeNull();
            } else {
                out.append(Double.toString(item));
            }

            out.append(',');
        }

        double item = array[end];

        if (Double.isNaN(item)) {
            out.writeNull();
        } else {
            out.append(Double.toString(item));
        }

        out.append(']');
    }
}
