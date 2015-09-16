
package org.generationcp.commons.ruleengine.stockid;

import junit.framework.Assert;

import org.generationcp.commons.ruleengine.RuleException;
import org.generationcp.middleware.service.api.InventoryService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class StockNotationNumberRuleTest {

	private static final int TEST_NOTATION_NUMBER = 3;

	private StockNotationNumberRule unitUnderTest;
	private StockIDGenerationRuleExecutionContext ruleContext;
	private InventoryService inventoryService;

	@Before
	public void setUp() throws Exception {
		unitUnderTest = new StockNotationNumberRule();

		inventoryService = Mockito.mock(InventoryService.class);
		ruleContext = new StockIDGenerationRuleExecutionContext(null, inventoryService);
		ruleContext.setBreederIdentifier("DV");
	}

	@Test
	public void testStockNotation() throws RuleException {
		Mockito.when(inventoryService.getCurrentNotationNumberForBreederIdentifier(Mockito.anyString())).thenReturn(TEST_NOTATION_NUMBER);

		unitUnderTest.runRule(ruleContext);
		Assert.assertEquals("Unable to output the incremented value of the current notation number for input", new Integer(
				TEST_NOTATION_NUMBER + 1), ruleContext.getRuleExecutionOutput());
	}

	@Test
	public void testGetKey() {
		Assert.assertEquals(StockNotationNumberRule.KEY, unitUnderTest.getKey());
	}
}
