package com.lamfire.json;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.*;

import com.lamfire.json.serializer.JSONSerializer;
import com.lamfire.json.serializer.SerializeWriter;
import com.lamfire.json.util.TypeConverters;
import com.lamfire.utils.Lists;

public class JSONArray extends JSONParser implements List<Object>, JSONString, Cloneable, RandomAccess, Serializable {

    private static final long  serialVersionUID = 1L;
    private final List<Object> list;

    public JSONArray() {
        this.list = new ArrayList<Object>(10);
    }

    public JSONArray(List<Object> list) {
        this.list = list;
    }

    public JSONArray(int initialCapacity) {
        this.list = new ArrayList<Object>(initialCapacity);
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public boolean contains(Object o) {
        return list.contains(o);
    }

    public Iterator<Object> iterator() {
        return list.iterator();
    }

    public Object[] toArray() {
        return list.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    public boolean add(Object e) {
        return list.add(e);
    }

    public boolean remove(Object o) {
        return list.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    public boolean addAll(Collection<? extends Object> c) {
        return list.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends Object> c) {
        return list.addAll(index, c);
    }

    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    public void clear() {
        list.clear();
    }

    public Object set(int index, Object element) {
        return list.set(index, element);
    }

    public void add(int index, Object element) {
        list.add(index, element);
    }

    public Object remove(int index) {
        return list.remove(index);
    }

    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    public ListIterator<Object> listIterator() {
        return list.listIterator();
    }

    public ListIterator<Object> listIterator(int index) {
        return list.listIterator(index);
    }

    public List<Object> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    public Object get(int index) {
        return list.get(index);
    }

    public JSON getJSONObject(int index) {
        Object value = list.get(index);

        if (value instanceof JSON) {
            return (JSON) value;
        }

        return (JSON) toJSONObject(value);
    }

    public JSONArray getJSONArray(int index) {
        Object value = list.get(index);

        if (value instanceof JSONArray) {
            return (JSONArray) value;
        }

        return (JSONArray) toJSONObject(value);
    }

    public <T> T getObject(int index, Class<T> clazz) {
        Object obj = list.get(index);
        return TypeConverters.castToJavaBean(obj, clazz);
    }

    public Boolean getBoolean(int index) {
        Object value = get(index);

        if (value == null) {
            return null;
        }

        return TypeConverters.castToBoolean(value);
    }

    public boolean getBooleanValue(int index) {
        Object value = get(index);

        if (value == null) {
            return false;
        }

        return TypeConverters.castToBoolean(value).booleanValue();
    }

    public Byte getByte(int index) {
        Object value = get(index);

        return TypeConverters.castToByte(value);
    }

    public byte getByteValue(int index) {
        Object value = get(index);

        if (value == null) {
            return 0;
        }

        return TypeConverters.castToByte(value).byteValue();
    }

    public Short getShort(int index) {
        Object value = get(index);

        return TypeConverters.castToShort(value);
    }

    public short getShortValue(int index) {
        Object value = get(index);

        if (value == null) {
            return 0;
        }

        return TypeConverters.castToShort(value).shortValue();
    }

    public Integer getInteger(int index) {
        Object value = get(index);

        return TypeConverters.castToInt(value);
    }

    public int getIntValue(int index) {
        Object value = get(index);

        if (value == null) {
            return 0;
        }

        return TypeConverters.castToInt(value).intValue();
    }

    public Long getLong(int index) {
        Object value = get(index);

        return TypeConverters.castToLong(value);
    }

    public long getLongValue(int index) {
        Object value = get(index);

        if (value == null) {
            return 0L;
        }

        return TypeConverters.castToLong(value).longValue();
    }

    public Float getFloat(int index) {
        Object value = get(index);

        return TypeConverters.castToFloat(value);
    }

    public float getFloatValue(int index) {
        Object value = get(index);

        if (value == null) {
            return 0F;
        }

        return TypeConverters.castToFloat(value).floatValue();
    }

    public Double getDouble(int index) {
        Object value = get(index);

        return TypeConverters.castToDouble(value);
    }

    public double getDoubleValue(int index) {
        Object value = get(index);

        if (value == null) {
            return 0D;
        }

        return TypeConverters.castToDouble(value).floatValue();
    }

    public BigDecimal getBigDecimal(int index) {
        Object value = get(index);

        return TypeConverters.castToBigDecimal(value);
    }

    public BigInteger getBigInteger(int index) {
        Object value = get(index);

        return TypeConverters.castToBigInteger(value);
    }

    public String getString(int index) {
        Object value = get(index);

        return TypeConverters.castToString(value);
    }

    public java.util.Date getDate(int index) {
        Object value = get(index);

        return TypeConverters.castToDate(value);
    }

    public java.sql.Date getSqlDate(int index) {
        Object value = get(index);

        return TypeConverters.castToSqlDate(value);
    }

    public java.sql.Timestamp getTimestamp(int index) {
        Object value = get(index);

        return TypeConverters.castToTimestamp(value);
    }

    @Override
    public Object clone() {
        return new JSONArray(new ArrayList<Object>(list));
    }

    public boolean equals(Object obj) {
        return this.list.equals(obj);
    }

    public int hashCode() {
        return this.list.hashCode();
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

    public byte[] toBytes() {
        SerializeWriter out = new SerializeWriter();
        try {
            new JSONSerializer(out).write(this);
            return out.toBytes();
        } finally {
            out.close();
        }
    }

    public byte[] toBytes(Charset charset) {
        SerializeWriter out = new SerializeWriter();
        try {
            new JSONSerializer(out).write(this);
            return out.toBytes(charset);
        } finally {
            out.close();
        }
    }

    public byte[] toBytes(String charset) {
        SerializeWriter out = new SerializeWriter();
        try {
            new JSONSerializer(out).write(this);
            return out.toBytes(charset);
        } finally {
            out.close();
        }
    }

    public <E> List<E> asList() {
        return Lists.asList(this);
    }

    public void writeJSONString(Appendable appendable) {
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

    public static JSONArray fromJSONString(String json) {
        return parseArray(json);
    }
}
