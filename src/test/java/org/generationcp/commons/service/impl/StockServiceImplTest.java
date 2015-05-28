package org.generationcp.commons.service.impl;


import org.generationcp.commons.ruleengine.stockid.StockIDSeparatorRule;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.pojos.ListDataProject;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.generationcp.commons.exceptions.StockException;
import org.generationcp.commons.ruleengine.RuleFactory;
import org.generationcp.commons.ruleengine.service.RulesService;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.service.api.InventoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


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
	
	public static final String COMMA = ",";
	public static final String TEST_INVENTORY_ID = "TR1-123";
	public static final String TEST_INVENTORY_ID_2 = "TR1-124";
	public static final String TEST_INVENTORY_ID_3 = "TR1-125";
	public static final String[] TEST_INVENTORY_ID_ARRAY = {
		TEST_INVENTORY_ID,TEST_INVENTORY_ID_2,
		TEST_INVENTORY_ID_3,null,null
	};
	public static final String[] TEST_BULK_WITH_ARRAY = {
		TEST_INVENTORY_ID_2+COMMA+TEST_INVENTORY_ID_3,
		TEST_INVENTORY_ID+COMMA+TEST_INVENTORY_ID_3,
		TEST_INVENTORY_ID+COMMA+TEST_INVENTORY_ID_2,
		null, null
	};
	public static final String PLOT_DUPE_PREFIX = "Plot Dupe: ";
	public static final String[] TEST_DUPLICATE_ARRAY = {
		PLOT_DUPE_PREFIX+TEST_INVENTORY_ID_2+COMMA+TEST_INVENTORY_ID_3,
		PLOT_DUPE_PREFIX+TEST_INVENTORY_ID+COMMA+TEST_INVENTORY_ID_3,
		PLOT_DUPE_PREFIX+TEST_INVENTORY_ID+COMMA+TEST_INVENTORY_ID_2,
		null, null
	};
	private static final Integer TEST_LIST_ID = 17;

	@Resource
	private RulesService rulesService;

	@Resource
	private RuleFactory ruleFactory;

	@Resource
	private InventoryDataManager inventoryDataManager;
	
	@Resource
	private InventoryService inventoryService;

	@Resource
	private StockServiceImpl inventoryStockService;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		doReturn(createStockIdsTestData()).when(inventoryDataManager).
			getStockIdsByListDataProjectListId(TEST_LIST_ID);
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
	public void testVerifyIfBulkingForStockListCanProceed() throws Exception {
		List<InventoryDetails> inventoryDetails = createInventoryDetailsListTestData(true);
		inventoryStockService.
				verifyIfBulkingForStockListCanProceed(
						TEST_LIST_ID,inventoryDetails);
	}

	private List<String> createStockIdsTestData() {
		List<String> stockIDs = new ArrayList<String>();
		stockIDs.add(TEST_INVENTORY_ID);
		stockIDs.add(TEST_INVENTORY_ID_2);
		stockIDs.add(TEST_INVENTORY_ID_3);
		return stockIDs;
	}

	private List<InventoryDetails> createInventoryDetailsListTestData(boolean addBulkWith) {
		List<InventoryDetails> inventoryDetails = new ArrayList<InventoryDetails>();
		for(int i=0;i<TEST_BULK_WITH_ARRAY.length;i++) {
			inventoryDetails.add(createInventoryDetailsTestData(i,addBulkWith));
		}
		return inventoryDetails;
	}

	private InventoryDetails createInventoryDetailsTestData(int id,boolean addBulkWith) {
		InventoryDetails inventoryDetails = new InventoryDetails();
		inventoryDetails.setTrnId(id);
		inventoryDetails.setLotId(id);
		inventoryDetails.setEntryId(id);
		inventoryDetails.setGid(id);
		inventoryDetails.setLotGid(id);
		inventoryDetails.setSourceRecordId(id*10);
		inventoryDetails.setStockSourceRecordId(id*10);
		inventoryDetails.setInventoryID(TEST_INVENTORY_ID_ARRAY[id]);
		inventoryDetails.setDuplicate(TEST_DUPLICATE_ARRAY[id]);
		if(addBulkWith) {
			inventoryDetails.setBulkWith(TEST_BULK_WITH_ARRAY[id]);
			if(inventoryDetails.getBulkWith()!=null) {
				inventoryDetails.setBulkCompl("Y");
			}
		}
		return inventoryDetails;
	}
	
	@Test(expected=StockException.class)
	public void testVerifyIfBulkingForStockListCanProceed_NoBulkWith() throws Exception {
		List<InventoryDetails> inventoryDetails = createInventoryDetailsListTestData(false);
		inventoryStockService.
				verifyIfBulkingForStockListCanProceed(
						TEST_LIST_ID,inventoryDetails);
	}
	
	@Test(expected=StockException.class)
	public void testVerifyIfBulkingForStockListCanProceed_InvalidBulkWith() throws Exception {
		List<InventoryDetails> inventoryDetails = createInventoryDetailsListTestData(false);
		inventoryDetails.get(0).setBulkWith("any");
		inventoryStockService.
				verifyIfBulkingForStockListCanProceed(
						TEST_LIST_ID,inventoryDetails);
	}
	
	@Test(expected=StockException.class)
	public void testVerifyIfBulkingForStockListCanProceed_EntriesToBulkNotBulkComplete() throws Exception {
		List<InventoryDetails> inventoryDetails = createInventoryDetailsListTestData(true);
		inventoryDetails.get(0).setBulkCompl(null);
		inventoryStockService.
				verifyIfBulkingForStockListCanProceed(
						TEST_LIST_ID,inventoryDetails);
	}
	
	@Test(expected=StockException.class)
	public void testVerifyIfBulkingForStockListCanProceed_InvalidBulkCompl() throws Exception {
		List<InventoryDetails> inventoryDetails = createInventoryDetailsListTestData(true);
		inventoryDetails.get(0).setBulkCompl("wrong");
		inventoryStockService.
				verifyIfBulkingForStockListCanProceed(
						TEST_LIST_ID,inventoryDetails);
	}
	
	@Test(expected=StockException.class)
	public void testVerifyIfBulkingForStockListCanProceed_BulkComplButNullBulkWith() throws Exception {
		List<InventoryDetails> inventoryDetails = createInventoryDetailsListTestData(true);
		inventoryDetails.get(0).setBulkWith(null);
		inventoryStockService.
				verifyIfBulkingForStockListCanProceed(
						TEST_LIST_ID,inventoryDetails);
	}
	
	@Test
	public void testGetBulkingDonors() throws MiddlewareException {
		List<InventoryDetails> inventoryDetailsList = createInventoryDetailsListTestData(true);
		Map<String, Integer> stockIDEntryMap = new HashMap<String,Integer>();
		Map<Integer, InventoryDetails> entryInventoryDetailsMap = new HashMap<Integer,InventoryDetails>();
		inventoryStockService.buildMapsOfInventoryDetails(
				inventoryDetailsList, stockIDEntryMap, entryInventoryDetailsMap);
		List<InventoryDetails> bulkingDonors = new ArrayList<InventoryDetails>();
		List<InventoryDetails> bulkingRecipients = new ArrayList<InventoryDetails>();
		inventoryStockService.retrieveBulkingDonorsAndRecipients(
				inventoryDetailsList, stockIDEntryMap, entryInventoryDetailsMap, 
					bulkingDonors, bulkingRecipients);
		for (InventoryDetails inventoryDetails : bulkingDonors) {
			switch(inventoryDetails.getTrnId()) {
				case 0: 
				case 1: 
				case 2: assertEquals(0,inventoryDetails.getSourceRecordId().intValue()); break;
				default: break;
			}
			switch(inventoryDetails.getLotId()) {
				case 0: 
				case 1: 
				case 2: assertEquals(0,inventoryDetails.getLotGid().intValue()); break;
				default: break;
			}
			assertEquals(InventoryDetails.BULK_COMPL_COMPLETED,inventoryDetails.getBulkCompl());
		}
		for (InventoryDetails inventoryDetails : bulkingRecipients) {
			assertEquals(InventoryDetails.BULK_COMPL_COMPLETED,inventoryDetails.getBulkCompl());
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testExecuteBulkingInstructions() throws MiddlewareException {
		StockServiceImpl spy = spy(inventoryStockService);
		mockReturnedTransactions();
		mockReturnedLots();
		mockSaveChangesToBulkingDonors(spy);
		List<InventoryDetails> inventoryDetailsList = createInventoryDetailsListTestData(true);
		spy.executeBulkingInstructions(inventoryDetailsList);
		verify(spy).saveChangesInBulkingProcess(anyList(),anyList());
	}

	@SuppressWarnings("unchecked")
	private void mockSaveChangesToBulkingDonors(StockServiceImpl spy) throws MiddlewareQueryException {
		doNothing().when(spy).saveChangesInBulkingProcess(anyList(),anyList());
	}

	private void mockReturnedLots() throws MiddlewareQueryException {
		for(int i=0;i<TEST_BULK_WITH_ARRAY.length;i++) {
			doReturn(new Lot(i)).when(inventoryDataManager).
				getLotById(i);
		}
	}

	private void mockReturnedTransactions() throws MiddlewareQueryException {
		for(int i=0;i<TEST_BULK_WITH_ARRAY.length;i++) {
			doReturn(new Transaction(i)).when(inventoryDataManager).
				getTransactionById(i);
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
			if (inventoryDetails.getEntryId().equals(1)) {
				String expectedBulkWith = INVENTORY_ID_PREFIX + 20 + ", " +
						INVENTORY_ID_PREFIX + 2 + ", " +
						INVENTORY_ID_PREFIX + 4;
				assertEquals(expectedBulkWith, 
						inventoryDetails.getBulkWith());
				assertEquals("Y", inventoryDetails.getBulkCompl());
			} else if (inventoryDetails.getEntryId().equals(2)) {
				String expectedBulkWith = INVENTORY_ID_PREFIX + 20 + ", " +
						INVENTORY_ID_PREFIX + 1 + ", " +
						INVENTORY_ID_PREFIX + 4;
				assertEquals(expectedBulkWith, 
						inventoryDetails.getBulkWith());
				assertEquals("Y", inventoryDetails.getBulkCompl());
			} else if (inventoryDetails.getEntryId().equals(4)) {
				String expectedBulkWith = INVENTORY_ID_PREFIX + 20 + ", " +
						INVENTORY_ID_PREFIX + 1 + ", " +
						INVENTORY_ID_PREFIX + 2;
				assertEquals(expectedBulkWith, 
						inventoryDetails.getBulkWith());
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
