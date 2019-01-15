package org.generationcp.commons.ruleengine.coding.expression;

import org.generationcp.middleware.pojos.naming.NamingConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class SequenceExpressionTest {

	private final SequenceExpression sequenceExpression = new SequenceExpression();

	@Test
	public void testApply() {

		final int startingSequenceNumber = 1;
		final NamingConfiguration namingConfiguration = new NamingConfiguration();
		namingConfiguration.setSequenceCounter(startingSequenceNumber);
		final List<StringBuilder> values = new ArrayList<>();
		values.add(new StringBuilder("[SEQUENCE]"));

		assertEquals(startingSequenceNumber, namingConfiguration.getSequenceCounter());

		sequenceExpression.apply(values, "", namingConfiguration);

		assertEquals(String.valueOf(startingSequenceNumber), values.get(0).toString());
		assertEquals("Sequence Counter should be incremented each time [SEQUENCE] expression is evaluated.", startingSequenceNumber + 1,
				namingConfiguration.getSequenceCounter());

	}

	@Test
	public void testApplyValueHasStringLiteral() {

		final int startingSequenceNumber = 1;
		final NamingConfiguration namingConfiguration = new NamingConfiguration();
		namingConfiguration.setSequenceCounter(startingSequenceNumber);
		final List<StringBuilder> values = new ArrayList<>();
		values.add(new StringBuilder("AAA[SEQUENCE]BBB"));

		assertEquals(startingSequenceNumber, namingConfiguration.getSequenceCounter());

		sequenceExpression.apply(values, "", namingConfiguration);

		assertEquals("AAA" + String.valueOf(startingSequenceNumber) + "BBB", values.get(0).toString());
		assertEquals("Sequence Counter should be incremented each time [SEQUENCE] expression is evaluated.", startingSequenceNumber + 1,
				namingConfiguration.getSequenceCounter());

	}

}
