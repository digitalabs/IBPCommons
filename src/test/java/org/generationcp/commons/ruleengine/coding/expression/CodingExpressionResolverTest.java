package org.generationcp.commons.ruleengine.coding.expression;

import org.generationcp.middleware.pojos.naming.NamingConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class CodingExpressionResolverTest {

	@Mock
	private CodingExpressionFactory factory;

	@InjectMocks
	private CodingExpressionResolver codingExpressionResolver = new CodingExpressionResolver();

	@Test
	public void testResolve() {

		final int startingSequenceNumber = 1;
		final NamingConfiguration namingConfiguration = new NamingConfiguration();
		namingConfiguration.setSequenceCounter(startingSequenceNumber);
		final String currentInput = "CML";

		Mockito.when(factory.create(SequenceExpression.KEY)).thenReturn(new SequenceExpression());

		List<String> result = codingExpressionResolver.resolve(currentInput, SequenceExpression.KEY, namingConfiguration);

		assertEquals(currentInput + startingSequenceNumber, result.get(0));

	}

}
