package org.generationcp.commons.ruleengine.coding.expression;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CodingExpressionPostProcessorTest {

	private CodingExpressionPostProcessor codingExpressionPostProcessor = new CodingExpressionPostProcessor();

	@Mock
	private CodingExpressionFactory codingExpressionFactory;

	@Before
	public void init() {
		codingExpressionPostProcessor.setCodingExpressionFactory(codingExpressionFactory);
	}

	@Test
	public void testPostProcessAfterInitialization() {
		BaseCodingExpression expression = new SequenceExpression();
		codingExpressionPostProcessor.postProcessAfterInitialization(expression, null);
		verify(codingExpressionFactory).addExpression(expression);

	}

}
