package org.generationcp.commons.ruleengine.coding;

import org.generationcp.commons.ruleengine.RuleException;
import org.generationcp.commons.ruleengine.impl.CodingExpressionResolver;
import org.generationcp.middleware.pojos.workbench.NamingConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class SuffixRuleTest {

	@Mock
	private CodingExpressionResolver codingExpressionResolver;

	@InjectMocks
	private SuffixRule prefixRule;

	@Test
	public void testRunRule() throws RuleException {

		final String suffix = "XYZ";

		final List<String> sequenceOrder = new ArrayList<>();
		final NamingConfiguration namingConfiguration = new NamingConfiguration();

		namingConfiguration.setSuffix(suffix);
		final CodingRuleExecutionContext context = new CodingRuleExecutionContext(sequenceOrder, namingConfiguration);
		context.setCurrentData("");

		Mockito.when(codingExpressionResolver.resolve(context.getCurrentData(), namingConfiguration.getSuffix(), namingConfiguration))
				.thenReturn(Arrays.asList(suffix));

		assertEquals(suffix, this.prefixRule.runRule(context));
	}

}
