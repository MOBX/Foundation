package com.lamfire.utils;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class TypeConvertUtils {
	static final Map<Class<?>, Object> DefaultValues = new HashMap<Class<?>, Object>();

	static {
		DefaultValues.put(Boolean.TYPE, Boolean.FALSE);
		DefaultValues.put(Boolean.class, Boolean.FALSE);
		DefaultValues.put(Byte.TYPE, 0);
		DefaultValues.put(Byte.class, 0);
		DefaultValues.put(Short.TYPE, 0);
		DefaultValues.put(Short.class, 0);
		DefaultValues.put(Character.TYPE, new Character('\000'));
		DefaultValues.put(Integer.TYPE, new Integer(0));
		DefaultValues.put(Long.TYPE, new Long(0L));
		DefaultValues.put(Float.TYPE, new Float(0.0F));
		DefaultValues.put(Double.TYPE, new Double(0.0D));

		DefaultValues.put(BigInteger.class, new BigInteger("0"));
		DefaultValues.put(BigDecimal.class, new BigDecimal(0.0D));
	}

	@SuppressWarnings("unchecked")
	private static <T> T getDefaultValue(Class<?> forClass) {
		return (T) DefaultValues.get(forClass);
	}

	public static boolean toBoolean(boolean value) {
		return value;
	}

	public static boolean toBoolean(int value) {
		return value > 0;
	}

	public static boolean toBoolean(float value) {
		return value > 0;
	}

	public static boolean toBoolean(long value) {
		return value > 0;
	}

	public static boolean toBoolean(double value) {
		return value > 0;
	}

	public static boolean toBoolean(Object value) {
		if (value == null)
			return false;
		Class<?> c = value.getClass();

		if (c == Boolean.class)
			return ((Boolean) value).booleanValue();

		if (value instanceof Number)
			return ((Number) value).doubleValue() != 0;
		
		if ( c == String.class ){
			return Boolean.valueOf(value.toString());
		}

		if (c == Character.class)
			return ((Character) value).charValue() != 0;
		
		return true; // non-null
	}

	public static long toLong(Object value) throws NumberFormatException {
		if (value == null)
			return 0L;
		Class<?> c = value.getClass();
		if (c.getSuperclass() == Number.class)
			return ((Number) value).longValue();
		if (c == Boolean.class)
			return ((Boolean) value).booleanValue() ? 1 : 0;
		if (c == Character.class)
			return ((Character) value).charValue();
		return Long.parseLong(toString(value, true));
	}

	public static int toInt(Object value) throws NumberFormatException {
		if (value == null)
			return 0;
		Class<?> c = value.getClass();
		if (c.getSuperclass() == Number.class)
			return ((Number) value).intValue();
		if (c.getSuperclass() == String.class)
			return Integer.parseInt((String) value);
		if (c == Boolean.class)
			return ((Boolean) value).booleanValue() ? 1 : 0;
		if (c == Character.class)
			return ((Character) value).charValue();
		return Integer.parseInt(toString(value, true));
	}

	public static double toDouble(Object value) throws NumberFormatException {
		if (value == null)
			return 0.0;
		Class<?> c = value.getClass();
		if (c.getSuperclass() == Number.class)
			return ((Number) value).doubleValue();
		if (c == Boolean.class)
			return ((Boolean) value).booleanValue() ? 1 : 0;
		if (c == Character.class)
			return ((Character) value).charValue();
		String s = toString(value, true);

		return (s.length() == 0) ? 0.0 : Double.parseDouble(s);
	}

	public static float toFloat(Object value) throws NumberFormatException {
		if (value == null)
			return 0.0f;
		Class<?> c = value.getClass();
		if (c.getSuperclass() == Number.class)
			return ((Number) value).floatValue();
		if (c == Boolean.class)
			return ((Boolean) value).booleanValue() ? 1 : 0;
		if (c == Character.class)
			return ((Character) value).charValue();

		String s = toString(value, true);
		return (s.length() == 0) ? 0.0f : Float.parseFloat(s);
	}

	public static BigInteger toBigInteger(Object value) throws NumberFormatException {
		if (value == null)
			return BigInteger.valueOf(0L);
		Class<?> c = value.getClass();
		if (c == BigInteger.class)
			return (BigInteger) value;
		if (c == BigDecimal.class)
			return ((BigDecimal) value).toBigInteger();
		if (c.getSuperclass() == Number.class)
			return BigInteger.valueOf(((Number) value).longValue());
		if (c == Boolean.class)
			return BigInteger.valueOf(((Boolean) value).booleanValue() ? 1 : 0);
		if (c == Character.class)
			return BigInteger.valueOf(((Character) value).charValue());
		return new BigInteger(toString(value, true));
	}

	public static BigDecimal toBigDecimal(Object value) throws NumberFormatException {
		if (value == null)
			return BigDecimal.valueOf(0L);
		Class<?> c = value.getClass();
		if (c == BigDecimal.class)
			return (BigDecimal) value;
		if (c == BigInteger.class)
			return new BigDecimal((BigInteger) value);
		if (c == Boolean.class)
			return BigDecimal.valueOf(((Boolean) value).booleanValue() ? 1 : 0);
		if (c == Character.class)
			return BigDecimal.valueOf(((Character) value).charValue());
		return new BigDecimal(toString(value, true));
	}

	public static String toString(Object value, boolean trim) {
		String result;

		if (value == null) {
			result = null;
		} else {
			result = value.toString();
			if (trim) {
				result = result.trim();
			}
		}
		return result;
	}

	public static String toString(Object value) {
		return toString(value, false);
	}

	public static Object convert(char value, Class<?> toType) {
		return convert(new Character(value), toType);
	}

	public static Object convert(byte value, Class<?> toType) {
		return convert(new Byte(value), toType);
	}

	public static Object convert(int value, Class<?> toType) {
		return convert(new Integer(value), toType);
	}

	public static Object convert(long value, Class<?> toType) {
		return convert(new Long(value), toType);
	}

	public static Object convert(float value, Class<?> toType) {
		return convert(new Float(value), toType);
	}

	public static Object convert(double value, Class<?> toType) {
		return convert(new Double(value), toType);
	}

	public static Object convert(boolean value, Class<?> toType) {
		return convert(new Boolean(value), toType);
	}

	public static Object convert(char value, Class<?> toType, boolean preventNull) {
		return convert(new Character(value), toType, preventNull);
	}

	public static Object convert(byte value, Class<?> toType, boolean preventNull) {
		return convert(new Byte(value), toType, preventNull);
	}

	public static Object convert(int value, Class<?> toType, boolean preventNull) {
		return convert(new Integer(value), toType, preventNull);
	}

	public static Object convert(long value, Class<?> toType, boolean preventNull) {
		return convert(new Long(value), toType, preventNull);
	}

	public static Object convert(float value, Class<?> toType, boolean preventNull) {
		return convert(new Float(value), toType, preventNull);
	}

	public static Object convert(double value, Class<?> toType, boolean preventNull) {
		return convert(new Double(value), toType, preventNull);
	}

	public static Object convert(boolean value, Class<?> toType, boolean preventNull) {
		return convert(new Boolean(value), toType, preventNull);
	}


    public static Object convert(Object value, Class<?> toType) {
        return convert(value, toType, false);
    }

    public static Object convert(Object value, Class<?> toType, boolean preventNulls) {
        Object result = null;

        if (value != null && toType.isAssignableFrom(value.getClass())) {
            return value;
        }

        if (value == null) {
            if (toType.isPrimitive()) {
                result = getDefaultValue(toType);
            } else if (preventNulls && toType == Boolean.class) {
                result = Boolean.FALSE;
            } else if (preventNulls && Number.class.isAssignableFrom(toType)) {
                result = getDefaultValue(toType);
            }
            return result;
        }
        /* If array -> array then convert components of array individually */
        if (value.getClass().isArray() && toType.isArray()) {
            Class<?> componentType = toType.getComponentType();

            result = Array.newInstance(componentType, Array.getLength(value));
            for (int i = 0, icount = Array.getLength(value); i < icount; i++) {
                Array.set(result, i, convert(Array.get(value, i), componentType));
            }
        } else if (value.getClass().isArray() && !toType.isArray()) {

            return convert(Array.get(value, 0), toType);
        } else if (!value.getClass().isArray() && toType.isArray()) {

            if (toType.getComponentType() == Character.TYPE) {
                result = toString(value).toCharArray();
            } else if (toType.getComponentType() == Object.class) {
                return new Object[] { value };
            } else if (toType.getComponentType() == String.class) {
                return new String[] { toString(value) };
            } else if (toType.getComponentType() == Long.class) {
                return new Long[] { toLong(value) };
            } else if (toType.getComponentType() == Double.class) {
                return new Double[] { toDouble(value) };
            }
            if (toType.getComponentType() == Integer.class) {
                return new Integer[] { toInt(value) };
            }
            if (toType.getComponentType() == Float.class) {
                return new Float[] { toFloat(value) };
            }

        } else {
            if ((toType == Integer.class) || (toType == Integer.TYPE)) {
                return new Integer((int) toLong(value));
            }
            if ((toType == Double.class) || (toType == Double.TYPE))
                return new Double(toDouble(value));
            if ((toType == Boolean.class) || (toType == Boolean.TYPE))
                return toBoolean(value) ? Boolean.TRUE : Boolean.FALSE;
            if ((toType == Byte.class) || (toType == Byte.TYPE))
                return new Byte((byte) toLong(value));
            if ((toType == Character.class) || (toType == Character.TYPE))
                return new Character((char) toLong(value));
            if ((toType == Short.class) || (toType == Short.TYPE))
                return new Short((short) toLong(value));
            if ((toType == Long.class) || (toType == Long.TYPE))
                return new Long(toLong(value));
            if ((toType == Float.class) || (toType == Float.TYPE))
                return new Float(toDouble(value));
            if (toType == BigInteger.class)
                return toBigInteger(value);
            if (toType == BigDecimal.class)
                return toBigDecimal(value);
            if (toType == String.class)
                return toString(value);
        }

        if (result == null && preventNulls) {
            return value;
        }

        if(toType == Date.class){
            return new Date(toLong(value));
        }

        if (value != null && result == null) {
            throw new IllegalArgumentException("Unable to convert type " + value.getClass().getName() + " of " + value + " to type of " + toType.getName());
        }

        return result;
    }

	public static Object toArray(char value, Class<?> toType, boolean preventNull) {
		return toArray(new Character(value), toType, preventNull);
	}

	public static Object toArray(byte value, Class<?> toType, boolean preventNull) {
		return toArray(new Byte(value), toType, preventNull);
	}

	public static Object toArray(int value, Class<?> toType, boolean preventNull) {
		return toArray(new Integer(value), toType, preventNull);
	}

	public static Object toArray(long value, Class<?> toType, boolean preventNull) {
		return toArray(new Long(value), toType, preventNull);
	}

	public static Object toArray(float value, Class<?> toType, boolean preventNull) {
		return toArray(new Float(value), toType, preventNull);
	}

	public static Object toArray(double value, Class<?> toType, boolean preventNull) {
		return toArray(new Double(value), toType, preventNull);
	}

	public static Object toArray(boolean value, Class<?> toType, boolean preventNull) {
		return toArray(new Boolean(value), toType, preventNull);
	}

    public static Object toArray(char value, Class<?> toType) {
        return toArray(new Character(value), toType);
    }

    public static Object toArray(byte value, Class<?> toType) {
        return toArray(new Byte(value), toType);
    }

    public static Object toArray(int value, Class<?> toType) {
        return toArray(new Integer(value), toType);
    }

    public static Object toArray(long value, Class<?> toType) {
        return toArray(new Long(value), toType);
    }

    public static Object toArray(float value, Class<?> toType) {
        return toArray(new Float(value), toType);
    }

    public static Object toArray(double value, Class<?> toType) {
        return toArray(new Double(value), toType);
    }

    public static Object toArray(boolean value, Class<?> toType) {
        return toArray(new Boolean(value), toType);
    }

	public static Object toArray(Object value, Class<?> toType) {
		return toArray(value, toType, false);
	}

	public static Object toArray(Object value, Class<?> toType, boolean preventNulls) {
		if (value == null){
			return null;
        }

		if (value.getClass().isArray() && toType.isAssignableFrom(value.getClass().getComponentType())){
			return value;
        }

        Object result = null;

        //is to char
        if (toType == Character.TYPE){
            return toString(value).toCharArray();
        }

        // is collection
        if(ClassUtils.isCollectionType(value.getClass())){
            Collection col = (Collection)value;
            result = Array.newInstance(toType, col.size());
            Iterator it = col.iterator();
            int index = 0;
            while(it.hasNext()){
                Object o = it.next();
                Array.set(result, index++, convert(o, toType,preventNulls));
            }
            return result;
        }

        //is array
		if (value.getClass().isArray()) {
            int size = Array.getLength(value);
            result = Array.newInstance(toType, size);
            for (int i = 0, icount = size; i < icount; i++) {
                Array.set(result, i, convert(Array.get(value, i), toType,preventNulls));
            }
            return result;
		}


        //is single
        result = Array.newInstance(toType, 1);
        Array.set(result, 0, convert(value, toType, preventNulls));
		return result;
	}


}
