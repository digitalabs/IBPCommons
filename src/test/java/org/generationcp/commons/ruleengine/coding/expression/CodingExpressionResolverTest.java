package org.generationcp.commons.ruleengine.coding.expression;

import gherkin.formatter.Argument;
import org.generationcp.commons.service.GermplasmNamingService;
import org.generationcp.middleware.pojos.naming.NamingConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class CodingExpressionResolverTest {

	private static final String SEQUENCE_CODE = "[SEQUENCE]";

	@Mock
	private CodingExpressionFactory factory;

	@Mock
	private GermplasmNamingService germplasmNamingService;

	@InjectMocks
	private CodingExpressionResolver codingExpressionResolver = new CodingExpressionResolver();

	@Test
	public void testResolve() {

		final int startingSequenceNumber = 11;
		final NamingConfiguration namingConfiguration = new NamingConfiguration();
		final String prefix = "IBC";
		namingConfiguration.setPrefix(prefix);
		final String currentInput = "CML";

		final SequenceExpression sequenceExpression = new SequenceExpression();
		sequenceExpression.setGermplasmNamingService(this.germplasmNamingService);
		Mockito.when(factory.create(SEQUENCE_CODE)).thenReturn(sequenceExpression);
		Mockito.when(this.germplasmNamingService.getNextNumberAndIncrementSequence(prefix)).thenReturn(startingSequenceNumber);
		Mockito.when(this.germplasmNamingService.getNumberWithLeadingZeroesAsString(startingSequenceNumber, 1)).thenReturn(String.valueOf(startingSequenceNumber));

		final List<String> result = codingExpressionResolver.resolve(currentInput, SEQUENCE_CODE, namingConfiguration);
		assertEquals(currentInput + startingSequenceNumber, result.get(0));
	}

}
