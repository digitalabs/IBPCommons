package org.generationcp.commons.ruleengine.coding.expression;

import org.generationcp.commons.ruleengine.Expression;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
@RunWith(MockitoJUnitRunner.class)
public class CodingExpressionFactoryTest {

	CodingExpressionFactory factory = new CodingExpressionFactory();

	@Before
	public void init() {

		factory.init();
		factory.addExpression(new SequenceExpression());

	}

	@Test
	public void testLookup() {

		Assert.assertNotNull(factory.lookup(SequenceExpression.KEY));
		Assert.assertTrue(factory.lookup(SequenceExpression.KEY) instanceof Expression);
		Assert.assertNull(factory.lookup(""));

	}

	@Test
	public void testCreate() {

		Assert.assertNotNull(factory.create(SequenceExpression.KEY));
		Assert.assertTrue(factory.create(SequenceExpression.KEY) instanceof Expression);

	}

}
