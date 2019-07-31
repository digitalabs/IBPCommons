package org.generationcp.commons.ruleengine;

import junit.framework.Assert;
import org.generationcp.commons.ruleengine.naming.expression.Expression;
import org.generationcp.commons.ruleengine.naming.expression.FirstExpression;
import org.junit.Test;

public class ExpressionUtilsTest {

	private Expression unitUnderTest = new FirstExpression();

	@Test
	public void testReplaceProcessCodeWithValue() {
		final String key = unitUnderTest.getExpressionKey();
		StringBuilder builder = new StringBuilder("ABC" + key);

		ExpressionUtils.replaceExpressionWithValue(key, builder, "D");

		Assert.assertEquals("BaseExpression unable to replace the process code with the new value", "ABCD", builder.toString());
	}

	@Test
	public void testReplaceProcessCodeWithNullVariable() {
		final String key = unitUnderTest.getExpressionKey();
		StringBuilder builder = new StringBuilder("ABC" + key);

		String nullVariable = null;
		ExpressionUtils.replaceExpressionWithValue(key, builder, nullVariable);

		Assert.assertEquals("BaseExpression unable to replace the process code with the new value", "ABC", builder.toString());
	}

}
