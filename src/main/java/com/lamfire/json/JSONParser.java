package com.lamfire.json;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.util.*;

import com.lamfire.json.parser.*;
import com.lamfire.json.serializer.*;
import com.lamfire.json.util.*;

abstract class JSONParser {

    public static final int DEFAULT_PARSER_FEATURE;
    static {
        int features = 0;
        features |= Feature.AutoCloseSource.getMask();
        features |= Feature.InternFieldNames.getMask();
        features |= Feature.UseBigDecimal.getMask();
        features |= Feature.AllowUnQuotedFieldNames.getMask();
        features |= Feature.AllowSingleQuotes.getMask();
        features |= Feature.AllowArbitraryCommas.getMask();
        features |= Feature.SortFeidFastMatch.getMask();
        features |= Feature.IgnoreNotMatch.getMask();
        DEFAULT_PARSER_FEATURE = features;
    }

    public static final int DEFAULT_GENERATE_FEATURE;
    static {
        int features = 0;
        features |= com.lamfire.json.serializer.SerializerFeature.QuoteFieldNames.getMask();
        features |= com.lamfire.json.serializer.SerializerFeature.SkipTransientField.getMask();
        features |= com.lamfire.json.serializer.SerializerFeature.SortField.getMask();
        DEFAULT_GENERATE_FEATURE = features;
    }

    protected static final Object parse(String text) {
        return parse(text, DEFAULT_PARSER_FEATURE);
    }

    protected static final Object parse(String text, int features) {
        if (text == null) {
            return null;
        }

        DefaultJSONParser parser = new DefaultJSONParser(text, ParserConfig.getGlobalInstance(), features);
        Object value = parser.parse();

        parser.close();

        return value;
    }

    protected static final Object parse(byte[] input, Feature... features) {
        return parse(input, 0, input.length, UTF8_CharsetEncoder, features);
    }

    protected static final Object parse(byte[] input, int off, int len, CharsetDecoder charsetDecoder,
                                        Feature... features) {
        if (input == null || input.length == 0) {
            return null;
        }

        int featureValues = DEFAULT_PARSER_FEATURE;
        for (Feature featrue : features) {
            featureValues = Feature.config(featureValues, featrue, true);
        }

        return parse(input, off, len, charsetDecoder, featureValues);
    }

    protected static final Object parse(byte[] input, int off, int len, CharsetDecoder charsetDecoder, int features) {
        charsetDecoder.reset();

        int scaleLength = (int) (len * (double) charsetDecoder.maxCharsPerByte());
        char[] chars = ThreadLocalCache.getChars(scaleLength);

        ByteBuffer byteBuf = ByteBuffer.wrap(input, off, len);
        CharBuffer charBuf = CharBuffer.wrap(chars);
        CharUtils.decode(charsetDecoder, byteBuf, charBuf);

        int position = charBuf.position();

        DefaultJSONParser parser = new DefaultJSONParser(chars, position, ParserConfig.getGlobalInstance(), features);
        Object value = parser.parse();

        parser.close();

        return value;
    }

    protected static final Object parse(String text, Feature... features) {
        int featureValues = DEFAULT_PARSER_FEATURE;
        for (Feature featrue : features) {
            featureValues = Feature.config(featureValues, featrue, true);
        }

        return parse(text, featureValues);
    }

    protected static final JSON parseObject(String text, Feature... features) {
        return (JSON) parse(text, features);
    }

    protected static final JSON parseObject(String text) {
        return (JSON) parse(text);
    }

    @SuppressWarnings("unchecked")
    protected static final <T> T parseObject(String text, TypeReference<T> type, Feature... features) {
        return (T) parseObject(text, type.getType(), ParserConfig.getGlobalInstance(), DEFAULT_PARSER_FEATURE, features);
    }

    @SuppressWarnings("unchecked")
    protected static final <T> T parseObject(String text, Class<T> clazz, Feature... features) {
        return (T) parseObject(text, (Type) clazz, ParserConfig.getGlobalInstance(), DEFAULT_PARSER_FEATURE, features);
    }

    @SuppressWarnings("unchecked")
    protected static final <T> T parseObject(String input, Type clazz, Feature... features) {
        return (T) parseObject(input, clazz, ParserConfig.getGlobalInstance(), DEFAULT_PARSER_FEATURE, features);
    }

    @SuppressWarnings("unchecked")
    protected static final <T> T parseObject(String input, Type clazz, int featureValues, Feature... features) {
        if (input == null) {
            return null;
        }

        for (Feature featrue : features) {
            featureValues = Feature.config(featureValues, featrue, true);
        }

        DefaultExtJSONParser parser = new DefaultExtJSONParser(input, ParserConfig.getGlobalInstance(), featureValues);
        T value = (T) parser.parseObject(clazz);

        parser.close();

        return (T) value;
    }

    @SuppressWarnings("unchecked")
    protected static final <T> T parseObject(String input, Type clazz, ParserConfig config, int featureValues,
                                             Feature... features) {
        if (input == null) {
            return null;
        }

        for (Feature featrue : features) {
            featureValues = Feature.config(featureValues, featrue, true);
        }

        DefaultExtJSONParser parser = new DefaultExtJSONParser(input, config, featureValues);
        T value = (T) parser.parseObject(clazz);

        if (clazz != JSONArray.class) {
            parser.close();
        }

        return (T) value;
    }

    @SuppressWarnings("unchecked")
    protected static final <T> T parseObject(byte[] input, Type clazz, Feature... features) {
        return (T) parseObject(input, 0, input.length, UTF8_CharsetEncoder, clazz, features);
    }

    public static CharsetDecoder UTF8_CharsetEncoder = new UTF8Decoder(); // =
                                                                          // Charset.forName("UTF-8").newDecoder();

    @SuppressWarnings("unchecked")
    protected static final <T> T parseObject(byte[] input, int off, int len, CharsetDecoder charsetDecoder, Type clazz,
                                             Feature... features) {
        charsetDecoder.reset();

        int scaleLength = (int) (len * (double) charsetDecoder.maxCharsPerByte());
        char[] chars = ThreadLocalCache.getChars(scaleLength);

        ByteBuffer byteBuf = ByteBuffer.wrap(input, off, len);
        CharBuffer charByte = CharBuffer.wrap(chars);
        CharUtils.decode(charsetDecoder, byteBuf, charByte);

        int position = charByte.position();

        return (T) parseObject(chars, position, clazz, features);
    }

    @SuppressWarnings("unchecked")
    protected static final <T> T parseObject(char[] input, int length, Type clazz, Feature... features) {
        if (input == null || input.length == 0) {
            return null;
        }

        int featureValues = DEFAULT_PARSER_FEATURE;
        for (Feature featrue : features) {
            featureValues = Feature.config(featureValues, featrue, true);
        }

        DefaultExtJSONParser parser = new DefaultExtJSONParser(input, length, ParserConfig.getGlobalInstance(),
                                                               featureValues);
        T value = (T) parser.parseObject(clazz);

        parser.close();

        return (T) value;
    }

    protected static final <T> T parseObject(String text, Class<T> clazz) {
        return parseObject(text, clazz, new Feature[0]);
    }

    protected static final JSONArray parseArray(String text) {
        return (JSONArray) parse(text);
    }

    protected static final <T> List<T> parseArray(String text, Class<T> clazz) {
        if (text == null) {
            return null;
        }

        List<T> list = new ArrayList<T>();

        DefaultExtJSONParser parser = new DefaultExtJSONParser(text, ParserConfig.getGlobalInstance());
        parser.parseArray(clazz, list);

        parser.close();

        return list;
    }

    protected static final List<Object> parseArray(String text, Type[] types) {
        if (text == null) {
            return null;
        }

        List<Object> list;

        DefaultExtJSONParser parser = new DefaultExtJSONParser(text, ParserConfig.getGlobalInstance());
        list = Arrays.asList(parser.parseArray(types));

        parser.close();

        return list;
    }

    // ======================

    public static final String toJSONString(Object object) {
        return toJSONString(object, new SerializerFeature[0]);
    }

    protected static synchronized final String toJSONString(Object object, SerializerFeature... features) {
        SerializeWriter out = new SerializeWriter();

        try {
            JSONSerializer serializer = new JSONSerializer(out);
            for (SerializerFeature feature : features) {
                serializer.config(feature, true);
            }

            serializer.write(object);

            return out.toString();
        } catch (StackOverflowError e) {
            throw new JSONException("maybe circular references", e);
        } finally {
            out.close();
        }
    }

    protected static synchronized final String toJSONString(Object object, SerializeConfig config,
                                                            SerializerFeature... features) {
        SerializeWriter out = new SerializeWriter();

        try {
            JSONSerializer serializer = new JSONSerializer(out, config);
            for (SerializerFeature feature : features) {
                serializer.config(feature, true);
            }

            serializer.write(object);

            return out.toString();
        } catch (StackOverflowError e) {
            throw new JSONException("maybe circular references", e);
        } finally {
            out.close();
        }
    }

    protected static final String toJSONStringZ(Object object, SerializeConfig mapping, SerializerFeature... features) {
        SerializeWriter out = new SerializeWriter(features);

        try {
            JSONSerializer serializer = new JSONSerializer(out, mapping);

            serializer.write(object);

            return out.toString();
        } catch (StackOverflowError e) {
            throw new JSONException("maybe circular references", e);
        } finally {
            out.close();
        }
    }

    protected static final String toJSONString(Object object, boolean prettyFormat) {
        if (!prettyFormat) {
            return toJSONString(object);
        }

        return toJSONString(object, SerializerFeature.PrettyFormat);
    }

    // ///////

    protected static final Object toJSONObject(Object javaObject) {
        return toJSONObject(javaObject, ParserConfig.getGlobalInstance());
    }

    @SuppressWarnings("unchecked")
    protected static final Object toJSONObject(Object javaObject, ParserConfig mapping) {
        if (javaObject == null) {
            return null;
        }

        if (javaObject instanceof JSONParser) {
            return (JSONParser) javaObject;
        }

        if (javaObject instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) javaObject;

            JSON json = new JSON(map.size());

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Object jsonValue = toJSONObject(entry.getValue());
                json.put(entry.getKey(), jsonValue);
            }

            return json;
        }

        if (javaObject instanceof Collection) {
            Collection<Object> collection = (Collection<Object>) javaObject;

            JSONArray array = new JSONArray(collection.size());

            for (Object item : collection) {
                Object jsonValue = toJSONObject(item);
                array.add(jsonValue);
            }

            return array;
        }

        Class<?> clazz = javaObject.getClass();

        if (clazz.isArray()) {
            int len = Array.getLength(javaObject);

            JSONArray array = new JSONArray(len);

            for (int i = 0; i < len; ++i) {
                Object item = Array.get(javaObject, i);
                Object jsonValue = toJSONObject(item);
                array.add(jsonValue);
            }

            return array;
        }

        if (mapping.isPrimitive(clazz)) {
            return javaObject;
        }

        try {
            List<FieldInfo> getters = JavaBeanSerializer.computeGetters(clazz, null);

            JSON json = new JSON(getters.size());

            for (FieldInfo field : getters) {
                Method method = field.getMethod();
                Object value = method.invoke(javaObject, new Object[0]);
                Object jsonValue = toJSONObject(value);

                json.put(field.getName(), jsonValue);
            }

            return json;
        } catch (Exception e) {
            throw new JSONException("toJSON error", e);
        }
    }

}
