package org.generationcp.commons.ruleengine.coding.expression;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
public class CodingExpressionFactoryTest {

	private static final String KEY = "[SEQUENCE]";

	private final CodingExpressionFactory factory = new CodingExpressionFactory();

	@Before
	public void init() {

		factory.init();
		factory.addExpression(new SequenceExpression());
	}

	@Test
	public void testLookup() {
		Assert.assertNotNull(factory.lookup(CodingExpressionFactoryTest.KEY));
		Assert.assertNull(factory.lookup(""));

	}

}
