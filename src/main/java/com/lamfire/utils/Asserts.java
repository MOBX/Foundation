package com.lamfire.utils;

import java.lang.reflect.Array;

public class Asserts {
	public static void trueAssert(String message, boolean condition) {
		if (!condition)
			fail(message);
	}

	public static void trueAssert(boolean condition) {
        trueAssert(null, condition);
	}

	public static void falseAssert(String message, boolean condition) {
        trueAssert(message, !condition);
	}

	public static void falseAssert(boolean condition) {
        falseAssert(null, condition);
	}

	public static void fail(String message) {
		throw new AssertionError(message == null ? "" : message);
	}

	public static void fail() {
		fail(null);
	}

	public static void equalsAssert(String message, Object expected, Object actual) {
		if ((expected == null) && (actual == null))
			return;
		if ((expected != null) && (isEquals(expected, actual)))
			return;
		if (((expected instanceof String)) && ((actual instanceof String))) {
			String cleanMessage = message == null ? "" : message;
			throw new RuntimeException(cleanMessage + "_" + expected + "_" + actual);
		}

		failNotEquals(message, expected, actual);
	}

	private static boolean isEquals(Object expected, Object actual) {
		return expected.equals(actual);
	}

	public static void equalsAssert(Object expected, Object actual) {
        equalsAssert(null, expected, actual);
	}

	public static void equalsArrayAssert(String message, Object[] expecteds, Object[] actuals)

	{
		internalArrayEquals(message, expecteds, actuals);
	}

	public static void equalsArrayAssert(Object[] expecteds, Object[] actuals) {
        equalsArrayAssert(null, expecteds, actuals);
	}

	public static void equalsArrayAssert(String message, byte[] expecteds, byte[] actuals)

	{
		internalArrayEquals(message, expecteds, actuals);
	}

	public static void equalsArrayAssert(byte[] expecteds, byte[] actuals) {
        equalsArrayAssert(null, expecteds, actuals);
	}

	public static void equalsArrayAssert(String message, char[] expecteds, char[] actuals)

	{
		internalArrayEquals(message, expecteds, actuals);
	}

	public static void equalsArrayAssert(char[] expecteds, char[] actuals) {
        equalsArrayAssert(null, expecteds, actuals);
	}

	public static void equalsArrayAssert(String message, short[] expecteds, short[] actuals)

	{
		internalArrayEquals(message, expecteds, actuals);
	}

	public static void equalsArrayAssert(short[] expecteds, short[] actuals) {
        equalsArrayAssert(null, expecteds, actuals);
	}

	public static void equalsArrayAssert(String message, int[] expecteds, int[] actuals)

	{
		internalArrayEquals(message, expecteds, actuals);
	}

	public static void equalsArrayAssert(int[] expecteds, int[] actuals) {
        equalsArrayAssert(null, expecteds, actuals);
	}

	public static void equalsArrayAssert(String message, long[] expecteds, long[] actuals)

	{
		internalArrayEquals(message, expecteds, actuals);
	}

	public static void equalsArrayAssert(long[] expecteds, long[] actuals) {
        equalsArrayAssert(null, expecteds, actuals);
	}

	private static void internalArrayEquals(String message, Object expecteds, Object actuals)

	{
		if (expecteds == actuals)
			return;
		String header = message + ": ";
		if (expecteds == null)
			fail(header + "expected array was null");
		if (actuals == null)
			fail(header + "actual array was null");
		int actualsLength = Array.getLength(actuals);
		int expectedsLength = Array.getLength(expecteds);
		if (actualsLength != expectedsLength) {
			fail(header + "array lengths differed, expected.length=" + expectedsLength + " actual.length=" + actualsLength);
		}

		for (int i = 0; i < expectedsLength; i++) {
			Object expected = Array.get(expecteds, i);
			Object actual = Array.get(actuals, i);
			if ((isArray(expected)) && (isArray(actual))){
				internalArrayEquals(message, expected, actual);
            }else{
				equalsAssert(expected, actual);
            }
		}
	}

	private static boolean isArray(Object expected) {
		return (expected != null) && (expected.getClass().isArray());
	}

	public static void equalsAssert(String message, double expected, double actual, double delta) {
		if (Double.compare(expected, actual) == 0)
			return;
		if (Math.abs(expected - actual) > delta)
			failNotEquals(message, new Double(expected), new Double(actual));
	}

	public static void equalsAssert(long expected, long actual) {
        equalsAssert(null, expected, actual);
	}

	public static void equalsAssert(String message, long expected, long actual) {
        equalsAssert(message, Long.valueOf(expected), Long.valueOf(actual));
	}

	
	public static void equalsAssert(double expected, double actual) {
        equalsAssert(null, expected, actual);
	}


	public static void equalsAssert(String message, double expected, double actual) {
		fail("Use equalsAssert(expected, actual, delta) to compare floating-point numbers");
	}

	public static void equalsAssert(double expected, double actual, double delta) {
        equalsAssert(null, expected, actual, delta);
	}

	public static void notNullAssert(String message, Object object) {
		trueAssert(message, object != null);
	}

	public static void notNullAssert(Object object) {
        notNullAssert(null, object);
	}

	public static void nullAssert(String message, Object object) {
        trueAssert(message, object == null);
	}

	public static void nullAssert(Object object) {
        nullAssert(null, object);
	}

	public static void sameAssert(String message, Object expected, Object actual) {
		if (expected == actual)
			return;
		failNotSame(message, expected, actual);
	}

	public static void sameAssert(Object expected, Object actual) {
        sameAssert(null, expected, actual);
	}

	public static void notSameAssert(String message, Object unexpected, Object actual) {
		if (unexpected == actual){
			failSame(message);
        }
	}

	public static void notSameAssert(Object unexpected, Object actual) {
        notSameAssert(null, unexpected, actual);
	}

	private static void failSame(String message) {
		String formatted = "";
		if (message != null){
			formatted = message + " ";
        }
		fail(formatted + "expected not same");
	}

	private static void failNotSame(String message, Object expected, Object actual) {
		String formatted = "";
		if (message != null)
			formatted = message + " ";
		fail(formatted + "expected same:<" + expected + "> was not:<" + actual + ">");
	}

	private static void failNotEquals(String message, Object expected, Object actual) {
		fail(format(message, expected, actual));
	}

	static String format(String message, Object expected, Object actual) {
		String formatted = "";
		if ((message != null) && (!message.equals("")))
			formatted = message + " ";
		String expectedString = String.valueOf(expected);
		String actualString = String.valueOf(actual);
		if (expectedString.equals(actualString)) {
			return formatted + "expected: " + formatClassAndValue(expected, expectedString) + " but was: " + formatClassAndValue(actual, actualString);
		}

		return formatted + "expected:<" + expectedString + "> but was:<" + actualString + ">";
	}

	private static String formatClassAndValue(Object value, String valueString) {
		String className = value == null ? "null" : value.getClass().getName();
		return className + "<" + valueString + ">";
	}

	public static void equalsAssert(String message, Object[] expecteds, Object[] actuals) {
        equalsArrayAssert(message, expecteds, actuals);
	}

	public static void equalsAssert(Object[] expecteds, Object[] actuals) {
        equalsArrayAssert(expecteds, actuals);
	}
}