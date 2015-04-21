package org.generationcp.commons.service.impl;

import org.generationcp.commons.ruleengine.RuleFactory;
import org.generationcp.commons.ruleengine.service.RulesService;
import org.generationcp.commons.ruleengine.stockid.StockIDSeparatorRule;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.service.api.InventoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class InventoryStockServiceImplTest {

	public static final Integer DUMMY_NOTATION_NUMBER = 5;
	public static final String DUMMY_BREEDER_IDENTIFIER = "DV";

	public static final int DUMMY_DETAIL_COUNT = 5;
	public static final String EXPECTED_PREFIX = DUMMY_BREEDER_IDENTIFIER + (DUMMY_NOTATION_NUMBER + 1)
					+ StockIDSeparatorRule.DEFAULT_SEPARATOR;

	@Resource
	private RulesService rulesService;

	@Resource
	private RuleFactory ruleFactory;

	@Resource
	private InventoryService inventoryService;

	@Resource
	private InventoryStockServiceImpl inventoryStockService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testCalculateNextStockIDPrefix() throws MiddlewareException{
		when(inventoryService.getCurrentNotationNumberForBreederIdentifier(DUMMY_BREEDER_IDENTIFIER)).thenReturn(DUMMY_NOTATION_NUMBER);

		String prefix = inventoryStockService.calculateNextStockIDPrefix(DUMMY_BREEDER_IDENTIFIER, null);

		assertEquals(EXPECTED_PREFIX, prefix);
	}

	@Test
	public void testAssignStockIDs() throws MiddlewareException {
		when(inventoryService.getCurrentNotationNumberForBreederIdentifier(DUMMY_BREEDER_IDENTIFIER)).thenReturn(DUMMY_NOTATION_NUMBER);

		// create a dummy inventory detail list
		List<InventoryDetails> inventoryDetailsList = new ArrayList<>();

		// we only populate the entry ID since it is what's relevant to the logic
		for (int i = 0; i < DUMMY_DETAIL_COUNT; i++) {
			InventoryDetails details = new InventoryDetails();
			details.setEntryId(i + 1);
			inventoryDetailsList.add(details);
		}

		inventoryStockService.assignStockIDs(inventoryDetailsList, DUMMY_BREEDER_IDENTIFIER, null);

		for (InventoryDetails details : inventoryDetailsList) {
			assertEquals(EXPECTED_PREFIX + details.getEntryId(), details.getInventoryID());
		}
	}

}
