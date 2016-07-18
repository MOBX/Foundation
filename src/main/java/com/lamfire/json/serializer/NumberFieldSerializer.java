package com.lamfire.json.serializer;

import com.lamfire.json.util.FieldInfo;

class NumberFieldSerializer extends FieldSerializer {

    public NumberFieldSerializer(FieldInfo fieldInfo) {
        super(fieldInfo);
    }

    @Override
    public void writeProperty(JSONSerializer serializer, Object propertyValue) throws Exception {
        SerializeWriter out = serializer.getWriter();

        writePrefix(serializer);

        Object value = propertyValue;

        if (value == null) {
            if (out.isEnabled(SerializerFeature.WriteNullNumberAsZero)) {
                out.write('0');
            } else {
                out.writeNull();
            }
            return;
        }

        out.append(value.toString());
    }
}
