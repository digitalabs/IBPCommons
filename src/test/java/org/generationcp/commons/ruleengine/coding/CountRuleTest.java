package org.generationcp.commons.ruleengine.coding;

import org.generationcp.commons.ruleengine.RuleException;
import org.generationcp.commons.ruleengine.coding.expression.CodingExpressionResolver;
import org.generationcp.middleware.pojos.naming.NamingConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class CountRuleTest {

	@Mock
	private CodingExpressionResolver codingExpressionResolver;

	@InjectMocks
	private CountRule countRule;

	@Test
	public void testRunRule() throws RuleException {

		final List<String> sequenceOrder = new ArrayList<>();
		final NamingConfiguration namingConfiguration = new NamingConfiguration();
		namingConfiguration.setCount("[SEQUENCE]");
		final CodingRuleExecutionContext context = new CodingRuleExecutionContext(sequenceOrder, namingConfiguration);
		context.setCurrentData("CML");

		final String resolvedValue = "CML1";
		Mockito.when(codingExpressionResolver.resolve(context.getCurrentData(), namingConfiguration.getCount(), namingConfiguration))
				.thenReturn(Arrays.asList(resolvedValue));

		assertEquals(resolvedValue, this.countRule.runRule(context));
	}

}
