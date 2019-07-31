package org.generationcp.commons.ruleengine.coding.expression;

import junit.framework.Assert;
import org.junit.Test;

public class BaseCodingExpressionTest {
	private static final String SEQUENCE_CODE = "[SEQUENCE]";

	private SequenceExpression expression = new SequenceExpression();

	@Test
	public void testReplaceProcessCodeWithValue() {
		StringBuilder builder = new StringBuilder("ABC" + SEQUENCE_CODE);

		expression.replaceRegularExpressionKeyWithValue(builder, "D");

		Assert.assertEquals("BaseCodingExpression unable to replace the process code with the new value", "ABCD", builder.toString());
	}

	@Test
	public void testReplaceProcessCodeWithNullVariable() {
		StringBuilder builder = new StringBuilder("ABC" + SEQUENCE_CODE);

		String nullVariable = null;
		expression.replaceRegularExpressionKeyWithValue(builder, nullVariable);

		Assert.assertEquals("BaseCodingExpression unable to replace the process code with the new value", "ABC", builder.toString());
	}
}
