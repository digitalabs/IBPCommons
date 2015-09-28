
package org.generationcp.commons.util;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilTest {

	@Test
	public void testIsNumericForNonNumericString() {
		Assert.assertFalse("Expecting a false for non-numeric input.", StringUtil.isNumeric("non-numeric"));
		Assert.assertFalse("Expecting a false for an empty string input", StringUtil.isNumeric(""));
		Assert.assertFalse("Expecting a false for an input with alphanumeric characters", StringUtil.isNumeric("1TWO"));
	}

	@Test
	public void testIsNumericForNumericString() {
		Assert.assertTrue("Expecting a true for an input that consists only of digits", StringUtil.isNumeric("1234567890"));
		Assert.assertTrue("Expecting a true for an input in decimal form ", StringUtil.isNumeric("123456.7890"));
	}

}
