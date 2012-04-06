package ovation.odata.util;

import java.util.Arrays;

import junit.framework.Assert;

public class JUnitUtils {
	public static void assertEquals(byte[] expected, byte[] actual) {
		if (expected == actual) return;	// handles identical arrays and both null
		Assert.assertEquals(Arrays.toString(expected), Arrays.toString(actual));
	}
	public static void assertEquals(double[] expected, double[] actual) {
		if (expected == actual) return;	// handles identical arrays and both null
		Assert.assertEquals(Arrays.toString(expected), Arrays.toString(actual));
	}
	public static <T> void assertEquals(T[] expected, T[] actual) {
		if (expected == actual) return;	// handles identical arrays and both null
		Assert.assertEquals(Arrays.toString(expected), Arrays.toString(actual));
	}
}
