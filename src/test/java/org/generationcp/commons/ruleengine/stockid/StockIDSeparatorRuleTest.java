
package org.generationcp.commons.ruleengine.stockid;

import junit.framework.Assert;
import org.generationcp.commons.ruleengine.RuleException;
import org.junit.Before;
import org.junit.Test;

public class StockIDSeparatorRuleTest {

	public static final String TEST_SEPARATOR = "|";

	private StockIDSeparatorRule unitUnderTest;
	private StockIDGenerationRuleExecutionContext ruleContext;

	@Before
	public void setUp() {
		unitUnderTest = new StockIDSeparatorRule();
		ruleContext = new StockIDGenerationRuleExecutionContext(null);
	}

	@Test
	public void testNoSuppliedSeparator() throws RuleException {
		unitUnderTest.runRule(ruleContext);

		Assert.assertEquals("Expected rule to output the default separator", StockIDSeparatorRule.DEFAULT_SEPARATOR,
				ruleContext.getRuleExecutionOutput());
	}

	@Test
	public void testSeparatorSupplied() throws RuleException {
		ruleContext.setSeparator(TEST_SEPARATOR);

		unitUnderTest.runRule(ruleContext);

		Assert.assertEquals("Expected the rule to output the separator provided in the context", TEST_SEPARATOR,
				ruleContext.getRuleExecutionOutput());
	}

	@Test
	public void testGetKey() {
		Assert.assertEquals(StockIDSeparatorRule.KEY, unitUnderTest.getKey());
	}
}
