package com.lamfire.json.serializer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.lamfire.json.util.FieldInfo;
import com.lamfire.json.util.JSONField;

public abstract class FieldSerializer implements Comparable<FieldSerializer> {

    protected final FieldInfo fieldInfo;
    private final String      double_quoted_fieldPrefix;
    private final String      single_quoted_fieldPrefix;
    private final String      un_quoted_fieldPrefix;
    private boolean           writeNull = false;

    public FieldSerializer(FieldInfo fieldInfo){
        super();
        this.fieldInfo = fieldInfo;
        fieldInfo.getMethod().setAccessible(true);

        this.double_quoted_fieldPrefix = '"' + fieldInfo.getName() + "\":";

        this.single_quoted_fieldPrefix = '\'' + fieldInfo.getName() + "\':";

        this.un_quoted_fieldPrefix = fieldInfo.getName() + ":";

        JSONField annotation = fieldInfo.getAnnotation(JSONField.class);
        if (annotation != null) {
            for (SerializerFeature feature : annotation.serialzeFeatures()) {
                if (feature == SerializerFeature.WriteMapNullValue) {
                    writeNull = true;
                }
            }
        }
    }

    public boolean isWriteNull() {
        return writeNull;
    }

    public Field getField() {
        return fieldInfo.getField();
    }

    public String getName() {
        return fieldInfo.getName();
    }

    public Method getMethod() {
        return fieldInfo.getMethod();
    }

    public void writePrefix(JSONSerializer serializer) throws IOException {
        SerializeWriter out = serializer.getWriter();

        if (serializer.isEnabled(SerializerFeature.QuoteFieldNames)) {
            if (serializer.isEnabled(SerializerFeature.UseSingleQuotes)) {
                out.write(single_quoted_fieldPrefix);
            } else {
                out.write(double_quoted_fieldPrefix);
            }
        } else {
            out.write(un_quoted_fieldPrefix);
        }
    }

    public int compareTo(FieldSerializer o) {
        return this.getName().compareTo(o.getName());
    }

    public Object getPropertyValue(Object object) throws Exception {
        return getMethod().invoke(object);
    }

    public abstract void writeProperty(JSONSerializer serializer, Object propertyValue) throws Exception;

}
