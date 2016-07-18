package com.lamfire.json.serializer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@SuppressWarnings({ "rawtypes" })
public class MapSerializer implements ObjectSerializer {

    public static MapSerializer instance = new MapSerializer();

    public void write(JSONSerializer serializer, Object object) throws IOException {
        SerializeWriter out = serializer.getWriter();

        if (object == null) {
            out.writeNull();
            return;
        }

        Map<?, ?> map = (Map<?, ?>) object;

        // if (out.isEnabled(SerializerFeature.SortField)) {
        // map = new TreeMap(map);
        // }

        out.write('{');

        Class<?> preClazz = null;
        ObjectSerializer preWriter = null;

        boolean first = true;
        for (Map.Entry entry : map.entrySet()) {
            Object value = entry.getValue();

            Object entryKey = entry.getKey();
            String key = entryKey == null ? "null" : entryKey.toString();

            List<PropertyFilter> propertyFilters = serializer.getPropertyFiltersDirect();
            if (propertyFilters != null) {
                boolean apply = true;
                for (PropertyFilter propertyFilter : propertyFilters) {
                    if (!propertyFilter.apply(object, key, value)) {
                        apply = false;
                        break;
                    }
                }

                if (!apply) {
                    continue;
                }
            }

            List<NameFilter> nameFilters = serializer.getNameFiltersDirect();
            if (nameFilters != null) {
                for (NameFilter nameFilter : nameFilters) {
                    key = nameFilter.process(object, key, value);
                }
            }

            List<ValueFilter> valueFilters = serializer.getValueFiltersDirect();
            if (valueFilters != null) {
                for (ValueFilter valueFilter : valueFilters) {
                    value = valueFilter.process(object, key, value);
                }
            }

            if (value == null) {
                if (!serializer.isEnabled(SerializerFeature.WriteMapNullValue)) {
                    continue;
                }
            }

            if (!first) {
                out.write(',');
            }

            out.writeFieldName(key);
            first = false;

            if (value == null) {
                out.writeNull();
                continue;
            }

            Class<?> clazz = value.getClass();

            if (clazz == preClazz) {
                preWriter.write(serializer, value);
            } else {
                preClazz = clazz;
                preWriter = serializer.getObjectWriter(clazz);

                preWriter.write(serializer, value);
            }
        }
        out.write('}');
    }
}
