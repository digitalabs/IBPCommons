package org.generationcp.commons.ruleengine;

import org.generationcp.commons.ruleengine.naming.expression.Expression;
import org.generationcp.commons.ruleengine.naming.expression.FirstExpression;
import org.generationcp.commons.ruleengine.naming.expression.PaddedSequenceExpression;
import org.junit.Assert;
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
	public void testReplaceProcessCodeWithNullValue() {
		final String key = unitUnderTest.getExpressionKey();
		StringBuilder builder = new StringBuilder("ABC" + key);

		String nullVariable = null;
		ExpressionUtils.replaceExpressionWithValue(key, builder, nullVariable);

		Assert.assertEquals("BaseExpression unable to replace the process code with the new value", "ABC", builder.toString());
	}

	@Test
	public void testReplaceRegularExpressionProcessCodeWithNullValue() {
		final PaddedSequenceExpression expression = new PaddedSequenceExpression();
		StringBuilder builder = new StringBuilder("ABC" + "[PADSEQ.3]");

		String nullVariable = null;
		ExpressionUtils.replaceRegularExpressionKeyWithValue(expression.getExpressionKey(), builder, nullVariable);

		Assert.assertEquals("BaseExpression unable to replace the process code with the new value", "ABC", builder.toString());
	}

	@Test
	public void testReplaceRegularExpressionProcessCodeWithValue() {
		final PaddedSequenceExpression expression = new PaddedSequenceExpression();
		StringBuilder builder = new StringBuilder("ABC" + "[PADSEQ.3]");

		final String value = "023";
		ExpressionUtils.replaceRegularExpressionKeyWithValue(expression.getExpressionKey(), builder, value);

		Assert.assertEquals("BaseExpression unable to replace the process code with the new value", "ABC" + value, builder.toString());
	}

}
