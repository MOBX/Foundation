package com.lamfire.json.serializer;

import java.io.File;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import com.lamfire.json.JSONException;
import com.lamfire.json.util.IdentityHashMap;

public class SerializeConfig extends IdentityHashMap<Type, ObjectSerializer> {

	private final static SerializeConfig globalInstance = new SerializeConfig();

	public ObjectSerializer createJavaBeanSerializer(Class<?> clazz) {
		if (!Modifier.isPublic(clazz.getModifiers())) {
			return new JavaBeanSerializer(clazz);
		}


		return new JavaBeanSerializer(clazz);
	}

	public final static SerializeConfig getGlobalInstance() {
		return globalInstance;
	}

	public SerializeConfig() {
		this(DEFAULT_TABLE_SIZE);
	}

	public SerializeConfig(int tableSize) {
		super(tableSize);

		put(Boolean.class, BooleanSerializer.instance);
		put(Character.class, CharacterSerializer.instance);
		put(Byte.class, IntegerSerializer.instance);
		put(Short.class, IntegerSerializer.instance);
		put(Integer.class, IntegerSerializer.instance);
		put(Long.class, LongSerializer.instance);
		put(Float.class, FloatSerializer.instance);
		put(Double.class, DoubleSerializer.instance);
		put(BigDecimal.class, BigDecimalSerializer.instance);
		put(BigInteger.class, BigIntegerSerializer.instance);
		put(String.class, StringSerializer.instance);
		put(byte[].class, ByteArraySerializer.instance);
		put(short[].class, ShortArraySerializer.instance);
		put(int[].class, IntArraySerializer.instance);
		put(long[].class, LongArraySerializer.instance);
		put(float[].class, FloatArraySerializer.instance);
		put(double[].class, DoubleArraySerializer.instance);
		put(boolean[].class, BooleanArraySerializer.instance);
		put(Object[].class, ObjectArraySerializer.instance);
		put(Class.class, ClassSerializer.instance);

		put(Locale.class, LocaleSerializer.instance);
		put(TimeZone.class, TimeZoneSerializer.instance);
		put(UUID.class, UUIDSerializer.instance);
		put(InetAddress.class, InetAddressSerializer.instance);
		put(Inet4Address.class, InetAddressSerializer.instance);
		put(Inet6Address.class, InetAddressSerializer.instance);
		put(InetSocketAddress.class, InetSocketAddressSerializer.instance);
		put(File.class, FileSerializer.instance);
		put(URI.class, URISerializer.instance);
		put(URL.class, URLSerializer.instance);
		put(Appendable.class, AppendableSerializer.instance);
		put(StringBuffer.class, AppendableSerializer.instance);
		put(StringBuilder.class, AppendableSerializer.instance);
		put(Pattern.class, PatternSerializer.instance);
		put(Charset.class, CharsetSerializer.instance);

		// atomic
		put(AtomicBoolean.class, AtomicBooleanSerializer.instance);
		put(AtomicInteger.class, AtomicIntegerSerializer.instance);
		put(AtomicLong.class, AtomicLongSerializer.instance);
		put(AtomicReference.class, AtomicReferenceSerializer.instance);
		put(AtomicIntegerArray.class, AtomicIntegerArraySerializer.instance);
		put(AtomicLongArray.class, AtomicLongArraySerializer.instance);

	}

}
