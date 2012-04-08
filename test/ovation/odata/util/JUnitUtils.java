package ovation.odata.util;

import java.util.Arrays;

import junit.framework.Assert;

public class JUnitUtils {
	public static void assertEquals(byte[] expected, byte[] actual) {
		assertEquals(null, expected, actual);
	}
	public static void assertEquals(String msg, byte[] expected, byte[] actual) {
		if (expected == actual) return;	// handles identical arrays and both null
		Assert.assertEquals(msg, Arrays.toString(expected), Arrays.toString(actual));
	}
	
	public static void assertEquals(String msg, boolean expected, Boolean actual) {
		Assert.assertNotNull(msg, actual);
		Assert.assertEquals(msg, expected, actual.booleanValue());
	}
	
	public static void assertEquals(double[] expected, double[] actual) {
		assertEquals(null, expected, actual);
	}
	public static void assertEquals(String msg, double[] expected, double[] actual) {
		if (expected == actual) return;	// handles identical arrays and both null
		Assert.assertEquals(msg, Arrays.toString(expected), Arrays.toString(actual));
	}
	
	public static <T> void assertEquals(T[] expected, T[] actual) {
		assertEquals(null, expected, actual);
	}
	public static <T> void assertEquals(String msg, T[] expected, T[] actual) {
		if (expected == actual) return;	// handles identical arrays and both null
		Assert.assertEquals(msg, Arrays.toString(expected), Arrays.toString(actual));
	}
}
