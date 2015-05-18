package org.generationcp.commons.service.impl;

import org.generationcp.commons.ruleengine.stockid.StockIDSeparatorRule;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.service.api.InventoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class StockServiceImplTest {

	public static final Integer DUMMY_NOTATION_NUMBER = 5;
	public static final String DUMMY_BREEDER_IDENTIFIER = "DV";

	public static final int DUMMY_DETAIL_COUNT = 5;
	public static final String EXPECTED_PREFIX = DUMMY_BREEDER_IDENTIFIER + (DUMMY_NOTATION_NUMBER + 1)
					+ StockIDSeparatorRule.DEFAULT_SEPARATOR;
	public static final String INVENTORY_ID_PREFIX = "DV-";

	@Resource
	private InventoryService inventoryService;

	@Resource
	private StockServiceImpl inventoryStockService;

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

	@Test
	public void testProcessBulkWithNoInstructions() {
		List<ListDataProject> testListData = createTestListDataProjectForBulking();
		Map<Integer, InventoryDetails> testDetails = createInventoryDetailsTestData();

		inventoryStockService.processBulkSettings(testListData, testDetails, false, false, false);

		for (InventoryDetails inventoryDetails : testDetails.values()) {
			assertNull(inventoryDetails.getBulkWith());
			assertNull(inventoryDetails.getBulkCompl());
		}
	}

	@Test
	public void testProcessBulkWithPedigreeDupe() {
		List<ListDataProject> testListData = createTestListDataProjectForBulking();
		Map<Integer, InventoryDetails> testDetails = createInventoryDetailsTestData();

		inventoryStockService.processBulkSettings(testListData, testDetails, true, false, false);

		for (InventoryDetails inventoryDetails : testDetails.values()) {
			if (inventoryDetails.getEntryId().equals(9)) {
				assertEquals(INVENTORY_ID_PREFIX + 10, inventoryDetails.getBulkWith());
				assertEquals("Y", inventoryDetails.getBulkCompl());
			} else if (inventoryDetails.getEntryId().equals(10)) {
				assertEquals(INVENTORY_ID_PREFIX + 9, inventoryDetails.getBulkWith());
				assertEquals("Y", inventoryDetails.getBulkCompl());
			} else{
				assertNull(inventoryDetails.getBulkWith());
				assertNull(inventoryDetails.getBulkCompl());
			}

		}
	}

	@Test
	public void testProcessBulkWithEntriesBothPlotDupeAndPlotReciprocal() {
		List<ListDataProject> testListData = createTestListDataProjectForBulking();
		Map<Integer, InventoryDetails> testDetails = createInventoryDetailsTestData();

		inventoryStockService.processBulkSettings(testListData, testDetails, false, true, false);
		for (InventoryDetails inventoryDetails : testDetails.values()) {
			if (inventoryDetails.getEntryId().equals(1) || inventoryDetails.getEntryId().equals(2) || inventoryDetails.getEntryId().equals(4)) {
				assertEquals(INVENTORY_ID_PREFIX + 20, inventoryDetails.getBulkWith());
				assertEquals("Y", inventoryDetails.getBulkCompl());
			} else if (inventoryDetails.getEntryId().equals(20)) {
				InventoryDetails dummy = new InventoryDetails();
				dummy.addBulkWith(testDetails.get(1).getInventoryID());
				dummy.addBulkWith(testDetails.get(2).getInventoryID());
				dummy.addBulkWith(testDetails.get(4).getInventoryID());
				assertEquals(dummy.getBulkWith(), inventoryDetails.getBulkWith());
				assertEquals("Y", inventoryDetails.getBulkCompl());
			}
		}
	}

	protected List<ListDataProject> createTestListDataProjectForBulking() {
		List<ListDataProject> dataProjectList = new ArrayList<>();

		ListDataProject ldp = new ListDataProject();
		ldp.setEntryId(1);
		ldp.setDuplicate(ListDataProject.PLOT_DUPE + ": 2,4");
		dataProjectList.add(ldp);

		ldp = new ListDataProject();
		ldp.setEntryId(2);
		ldp.setDuplicate(ListDataProject.PLOT_DUPE + ": 1,3");
		dataProjectList.add(ldp);

		ldp = new ListDataProject();
		ldp.setEntryId(3);
		ldp.setDuplicate(ListDataProject.PLOT_DUPE + ": 2,4");
		dataProjectList.add(ldp);

		ldp= new ListDataProject();
		ldp.setEntryId(4);
		ldp.setDuplicate(ListDataProject.PLOT_DUPE + ": 1,3");
		dataProjectList.add(ldp);

		ldp = new ListDataProject();
		ldp.setEntryId(5);
		ldp.setDuplicate(ListDataProject.PLOT_RECIP + ": 6,11");
		dataProjectList.add(ldp);

		ldp = new ListDataProject();
		ldp.setEntryId(6);
		ldp.setDuplicate(ListDataProject.PLOT_DUPE + ": 11");
		dataProjectList.add(ldp);

		ldp = new ListDataProject();
		ldp.setEntryId(7);
		ldp.setDuplicate(ListDataProject.PEDIGREE_RECIP + ": 8");
		dataProjectList.add(ldp);

		ldp = new ListDataProject();
		ldp.setEntryId(8);
		ldp.setDuplicate(ListDataProject.PEDIGREE_RECIP + ": 7");
		dataProjectList.add(ldp);

		ldp = new ListDataProject();
		ldp.setEntryId(9);
		ldp.setDuplicate(ListDataProject.PEDIGREE_DUPE + ": 10");
		dataProjectList.add(ldp);

		ldp = new ListDataProject();
		ldp.setEntryId(10);
		ldp.setDuplicate(ListDataProject.PEDIGREE_DUPE + ": 9");
		dataProjectList.add(ldp);

		ldp = new ListDataProject();
		ldp.setEntryId(11);
		ldp.setDuplicate(ListDataProject.PLOT_DUPE + ": 6");
		dataProjectList.add(ldp);

		ldp = new ListDataProject();
		ldp.setEntryId(20);
		ldp.setDuplicate(ListDataProject.PLOT_RECIP + ": 1,2,4");
		dataProjectList.add(ldp);

		return dataProjectList;
	}

	protected Map<Integer, InventoryDetails> createInventoryDetailsTestData() {
		Map<Integer, InventoryDetails> detailMap = new HashMap<>();

		addInventoryDetailTestData(1, detailMap);
		addInventoryDetailTestData(2, detailMap);
		addInventoryDetailTestData(3, detailMap);
		addInventoryDetailTestData(4, detailMap);
		addInventoryDetailTestData(5, detailMap);
		addInventoryDetailTestData(6, detailMap);
		addInventoryDetailTestData(7, detailMap);
		addInventoryDetailTestData(8, detailMap);
		addInventoryDetailTestData(9, detailMap);
		addInventoryDetailTestData(10, detailMap);
		addInventoryDetailTestData(11, detailMap);
		addInventoryDetailTestData(20, detailMap);

		return detailMap;
	}

	protected void addInventoryDetailTestData(Integer entryId, Map<Integer, InventoryDetails> detailMap) {
		InventoryDetails details = new InventoryDetails();
		details.setEntryId(entryId);
		details.setInventoryID(INVENTORY_ID_PREFIX + entryId);
		detailMap.put(entryId, details);
	}

}
