package com.lamfire.utils;

import java.math.BigInteger;
import java.util.Random;

import com.lamfire.code.Yarrow;

public class RandomUtils {
	private static final String STRINGS = "1234567890poiuytrewqasdfghjklmnbvcxzQWERTYUIOPLKJHGFDSAZXCVBNM";
	public static final Random RANDOM = new Random(System.currentTimeMillis());
	public static final Random yarrow = new Yarrow();

	public static int nextInt() {
		return nextInt(RANDOM);
	}

	public static int nextInt(Random random) {
		return random.nextInt();
	}

	public static int nextInt(int n) {
		return nextInt(RANDOM, n);
	}

	public static int nextInt(Random random, int n) {
		return random.nextInt(n);
	}

	public static long nextLong() {
		return nextLong(RANDOM);
	}

	public static long nextLong(Random random) {
		return random.nextLong();
	}

	public static boolean nextBoolean() {
		return nextBoolean(RANDOM);
	}

	public static boolean nextBoolean(Random random) {
		return random.nextBoolean();
	}

	public static float nextFloat() {
		return nextFloat(RANDOM);
	}

	public static float nextFloat(Random random) {
		return random.nextFloat();
	}

	public static double nextDouble() {
		return nextDouble(RANDOM);
	}

	public static double nextDouble(Random random) {
		return random.nextDouble();
	}

	public static String random(int bits, int radix) {
		return new BigInteger(bits, yarrow).toString(radix).toUpperCase();
	}

	public static String randomMACAddr() {
		return String.format("%02x:%02x:%02x:%02x:%02x:%02x", RandomUtils.nextInt(255), RandomUtils.nextInt(255), RandomUtils.nextInt(255), RandomUtils.nextInt(255), RandomUtils
				.nextInt(255), RandomUtils.nextInt(255));
	}

	public static String randomIPAddr() {
		return String.format("%d.%d.%d.%d", RandomUtils.nextInt(230), RandomUtils.nextInt(255), RandomUtils.nextInt(255), RandomUtils.nextInt(255));
	}
	
	public static String randomText(int maxLength) {
		int size = nextInt(maxLength +1);
		char[] chars = new char[size];
		for(int i=0;i<size;i++){
			chars[i] = STRINGS.charAt(nextInt(STRINGS.length()));
		}
		return String.valueOf(chars);
	}
	
	public static String randomText(int minLength ,int maxLength) {
		int size = minLength + nextInt(maxLength - minLength +1);
		char[] chars = new char[size];
		for(int i=0;i<size;i++){
			chars[i] = STRINGS.charAt(nextInt(STRINGS.length()));
		}
		return String.valueOf(chars);
	}

	public static String randomTextWithFixedLength(int size) {
		char[] chars = new char[size];
		for(int i=0;i<size;i++){
			chars[i] = STRINGS.charAt(nextInt(STRINGS.length()));
		}
		return String.valueOf(chars);
	}
}
