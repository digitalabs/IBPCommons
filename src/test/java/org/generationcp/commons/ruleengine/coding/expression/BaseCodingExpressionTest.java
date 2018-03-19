package org.generationcp.commons.ruleengine.coding.expression;

import junit.framework.Assert;
import org.junit.Test;

public class BaseCodingExpressionTest {

	private SequenceExpression expression = new SequenceExpression();

	@Test
	public void testReplaceProcessCodeWithValue() {
		StringBuilder builder = new StringBuilder("ABC" + expression.getExpressionKey());

		expression.replaceExpressionWithValue(builder, "D");

		Assert.assertEquals("BaseCodinExpression unable to replace the process code with the new value", "ABCD", builder.toString());
	}

	@Test
	public void testReplaceProcessCodeWithNullVariable() {
		StringBuilder builder = new StringBuilder("ABC" + expression.getExpressionKey());

		String nullVariable = null;
		expression.replaceExpressionWithValue(builder, nullVariable);

		Assert.assertEquals("BaseCodinExpression unable to replace the process code with the new value", "ABC", builder.toString());
	}
}
