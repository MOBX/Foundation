package com.lamfire.json;

import static com.lamfire.json.util.TypeConverters.castToBigDecimal;
import static com.lamfire.json.util.TypeConverters.castToBigInteger;
import static com.lamfire.json.util.TypeConverters.castToBoolean;
import static com.lamfire.json.util.TypeConverters.castToByte;
import static com.lamfire.json.util.TypeConverters.castToByteArray;
import static com.lamfire.json.util.TypeConverters.castToDate;
import static com.lamfire.json.util.TypeConverters.castToDouble;
import static com.lamfire.json.util.TypeConverters.castToFloat;
import static com.lamfire.json.util.TypeConverters.castToInt;
import static com.lamfire.json.util.TypeConverters.castToLong;
import static com.lamfire.json.util.TypeConverters.castToShort;
import static com.lamfire.json.util.TypeConverters.castToSqlDate;
import static com.lamfire.json.util.TypeConverters.castToTimestamp;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.lamfire.json.serializer.JSONSerializer;
import com.lamfire.json.serializer.SerializeWriter;
import com.lamfire.json.util.TypeConverters;

public class JSON extends JSONParser implements Map<String, Object>, JSONString, Cloneable, Serializable {

	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_INITIAL_CAPACITY = 16;

	private final Map<String, Object> map;

	public JSON() {
		this(DEFAULT_INITIAL_CAPACITY, true);
	}

	public JSON(Map<String, Object> map) {
		this.map = map;
	}

	public JSON(boolean ordered) {
		this(DEFAULT_INITIAL_CAPACITY, ordered);
	}

	public JSON(int initialCapacity) {
		this(initialCapacity, false);
	}

	public JSON(int initialCapacity, boolean ordered) {
		if (ordered) {
			map = new LinkedHashMap<String, Object>(initialCapacity);
		} else {
			map = new TreeMap<String, Object>();
		}
	}

	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public Object get(Object key) {
		return map.get(key);
	}

	public JSON getJSONObject(String key) {
		Object value = map.get(key);

		if (value instanceof JSON) {
			return (JSON) value;
		}

		return (JSON) toJSONObject(value);
	}

	public JSONArray getJSONArray(String key) {
		Object value = map.get(key);

		if (value instanceof JSONArray) {
			return (JSONArray) value;
		}

		return (JSONArray) toJSONObject(value);
	}

	public <T> T getObject(String key, Class<T> clazz) {
		Object obj = map.get(key);
		return TypeConverters.castToJavaBean(obj, clazz);
	}

	public Boolean getBoolean(String key) {
		Object value = get(key);

		if (value == null) {
			return null;
		}

		return castToBoolean(value);
	}

	public boolean getBooleanValue(String key) {
		Object value = get(key);

		if (value == null) {
			return false;
		}

		return castToBoolean(value).booleanValue();
	}

	public Byte getByte(String key) {
		Object value = get(key);

		return castToByte(value);
	}

    public byte[] getByteArray(String key) {
        Object value = get(key);

        return castToByteArray(value);
    }

	public byte getByteValue(String key) {
		Object value = get(key);

		if (value == null) {
			return 0;
		}

		return castToByte(value).byteValue();
	}

	public Short getShort(String key) {
		Object value = get(key);

		return castToShort(value);
	}

	public short getShortValue(String key) {
		Object value = get(key);

		if (value == null) {
			return 0;
		}

		return castToShort(value).shortValue();
	}

	public Integer getInteger(String key) {
		Object value = get(key);

		return castToInt(value);
	}

	public int getIntValue(String key) {
		Object value = get(key);

		if (value == null) {
			return 0;
		}

		return castToInt(value).intValue();
	}

	public Long getLong(String key) {
		Object value = get(key);

		return castToLong(value);
	}

	public long getLongValue(String key) {
		Object value = get(key);

		if (value == null) {
			return 0L;
		}

		return castToLong(value).longValue();
	}

	public Float getFloat(String key) {
		Object value = get(key);

		return castToFloat(value);
	}

	public float getFloatValue(String key) {
		Object value = get(key);

		if (value == null) {
			return 0F;
		}

		return castToFloat(value).floatValue();
	}

	public Double getDouble(String key) {
		Object value = get(key);

		return castToDouble(value);
	}

	public double getDoubleValue(String key) {
		Object value = get(key);

		if (value == null) {
			return 0D;
		}

		return castToDouble(value).floatValue();
	}

	public BigDecimal getBigDecimal(String key) {
		Object value = get(key);

		return castToBigDecimal(value);
	}

	public BigInteger getBigInteger(String key) {
		Object value = get(key);

		return castToBigInteger(value);
	}

	public String getString(String key) {
		Object value = get(key);

		if (value == null) {
			return null;
		}

		return value.toString();
	}

	public Date getDate(String key) {
		Object value = get(key);

		return castToDate(value);
	}

	public java.sql.Date getSqlDate(String key) {
		Object value = get(key);

		return castToSqlDate(value);
	}

	public java.sql.Timestamp getTimestamp(String key) {
		Object value = get(key);

		return castToTimestamp(value);
	}

	public Object put(String key, Object value) {
		return map.put(key, value);
	}

	public void putAll(Map<? extends String, ? extends Object> m) {
		map.putAll(m);
	}

	public void clear() {
		map.clear();
	}

	public Object remove(Object key) {
		return map.remove(key);
	}

	public Set<String> keySet() {
		return map.keySet();
	}

	public Collection<Object> values() {
		return map.values();
	}

	public Set<Entry<String, Object>> entrySet() {
		return map.entrySet();
	}

	@Override
	public Object clone() {
		return new JSON(new HashMap<String, Object>(map));
	}

	public boolean equals(Object obj) {
		return this.map.equals(obj);
	}

	public int hashCode() {
		return this.map.hashCode();
	}
	
    @Override
    public String toString() {
        return toJSONString();
    }

    public String toJSONString() {
        SerializeWriter out = new SerializeWriter();
        try {
            new JSONSerializer(out).write(this);
            return out.toString();
        } finally {
            out.close();
        }
    }

    public byte[] toBytes(){
        SerializeWriter out = new SerializeWriter();
        try {
            new JSONSerializer(out).write(this);
            return out.toBytes();
        } finally {
            out.close();
        }
    }

    public byte[] toBytes(Charset charset){
        SerializeWriter out = new SerializeWriter();
        try {
            new JSONSerializer(out).write(this);
            return out.toBytes(charset);
        } finally {
            out.close();
        }
    }

    public byte[] toBytes(String charset){
        SerializeWriter out = new SerializeWriter();
        try {
            new JSONSerializer(out).write(this);
            return out.toBytes(charset);
        } finally {
            out.close();
        }
    }

    public void write(Appendable appendable) {
        SerializeWriter out = new SerializeWriter();
        try {
            new JSONSerializer(out).write(this);
            appendable.append(out.toString());
        } catch (IOException e) {
            throw new JSONException(e.getMessage(), e);
        } finally {
            out.close();
        }
    }
    
    public <T> T toJavaObject(Class<T> clazz) {
    	return parseObject(this.toJSONString(), clazz);
    }
    
	/////////////////////////////////////////////////////////////////////////
	
    public static final <T> T toJavaObject(JSON json, Class<T> clazz) {
        return json.toJavaObject(clazz);
    }
    
    public static final <T> List<T> toJavaObjectArray(String json, Class<T> clazz) {
    	return parseArray(json, clazz);
    }

	public static <T> T toJavaObject(String json,Class<T> clazz){
		return parseObject(json, clazz);
	}

	public static JSON fromJSONString(String json){
		return parseObject(json);
	}
	
	public static JSON fromJavaObject(Object bean) {
		String json =  toJSONString(bean);
		return parseObject(json); 
	}

    public static JSON fromBytes(byte[] bytes){
        String js = new String(bytes);
        return fromJSONString(js);
    }

    public static JSON fromBytes(byte[] bytes,Charset charset){
        String js = new String(bytes,charset);
        return fromJSONString(js);
    }

    public static JSON fromBytes(byte[] bytes,String charset){
        String js = new String(bytes,Charset.forName(charset));
        return fromJSONString(js);
    }
}
