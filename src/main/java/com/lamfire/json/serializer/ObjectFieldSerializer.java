package com.lamfire.json.serializer;

import com.lamfire.json.util.FieldInfo;
import com.lamfire.json.util.JSONField;

public class ObjectFieldSerializer extends FieldSerializer {

    private ObjectSerializer fieldSerializer;
    private Class<?>         runtimeFieldClass;
    private String           format;

    public ObjectFieldSerializer(FieldInfo fieldInfo) {
        super(fieldInfo);

        JSONField annotation = fieldInfo.getAnnotation(JSONField.class);

        if (annotation != null) {
            format = annotation.format();

            if (format.trim().length() == 0) {
                format = null;
            }
        }
    }

    @Override
    public void writeProperty(JSONSerializer serializer, Object propertyValue) throws Exception {
        writePrefix(serializer);

        if (format != null) {
            serializer.writeWithFormat(propertyValue, format);
            return;
        }

        if (fieldSerializer == null) {

            if (propertyValue == null) {
                runtimeFieldClass = this.getMethod().getReturnType();
            } else {
                runtimeFieldClass = propertyValue.getClass();
            }

            fieldSerializer = serializer.getObjectWriter(runtimeFieldClass);
        }

        if (propertyValue == null) {
            fieldSerializer.write(serializer, null);
            return;
        }

        if (propertyValue.getClass() == runtimeFieldClass) {
            fieldSerializer.write(serializer, propertyValue);
            return;
        }

        serializer.write(propertyValue);
    }
}
