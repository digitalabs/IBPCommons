
package org.generationcp.commons.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.generationcp.commons.exceptions.StockException;
import org.generationcp.commons.ruleengine.RuleFactory;
import org.generationcp.commons.ruleengine.service.RulesService;
import org.generationcp.commons.ruleengine.stockid.StockIDSeparatorRule;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.service.api.InventoryService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class StockServiceImplTest {

	public static final Integer DUMMY_NOTATION_NUMBER = 5;
	public static final String DUMMY_BREEDER_IDENTIFIER = "DV";

	public static final int DUMMY_DETAIL_COUNT = 5;
	public static final String EXPECTED_PREFIX = StockServiceImplTest.DUMMY_BREEDER_IDENTIFIER
			+ (StockServiceImplTest.DUMMY_NOTATION_NUMBER + 1) + StockIDSeparatorRule.DEFAULT_SEPARATOR;

	public static final String INVENTORY_ID_PREFIX = "DV-";

	public static final String COMMA = ",";
	public static final String TEST_INVENTORY_ID = "TR1-123";
	public static final String TEST_INVENTORY_ID_2 = "TR1-124";
	public static final String TEST_INVENTORY_ID_3 = "TR1-125";
	public static final String[] TEST_INVENTORY_ID_ARRAY = {StockServiceImplTest.TEST_INVENTORY_ID,
			StockServiceImplTest.TEST_INVENTORY_ID_2, StockServiceImplTest.TEST_INVENTORY_ID_3, null, null};
	public static final String[] TEST_BULK_WITH_ARRAY = {
			StockServiceImplTest.TEST_INVENTORY_ID_2 + StockServiceImplTest.COMMA + StockServiceImplTest.TEST_INVENTORY_ID_3,
			StockServiceImplTest.TEST_INVENTORY_ID + StockServiceImplTest.COMMA + StockServiceImplTest.TEST_INVENTORY_ID_3,
			StockServiceImplTest.TEST_INVENTORY_ID + StockServiceImplTest.COMMA + StockServiceImplTest.TEST_INVENTORY_ID_2, null, null};
	public static final String PLOT_DUPE_PREFIX = "Plot Dupe: ";
	public static final String[] TEST_DUPLICATE_ARRAY = {
			StockServiceImplTest.PLOT_DUPE_PREFIX + StockServiceImplTest.TEST_INVENTORY_ID_2 + StockServiceImplTest.COMMA
					+ StockServiceImplTest.TEST_INVENTORY_ID_3,
			StockServiceImplTest.PLOT_DUPE_PREFIX + StockServiceImplTest.TEST_INVENTORY_ID + StockServiceImplTest.COMMA
					+ StockServiceImplTest.TEST_INVENTORY_ID_3,
			StockServiceImplTest.PLOT_DUPE_PREFIX + StockServiceImplTest.TEST_INVENTORY_ID + StockServiceImplTest.COMMA
					+ StockServiceImplTest.TEST_INVENTORY_ID_2,
			null, null};
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
		Mockito.doReturn(this.createStockIdsTestData()).when(this.inventoryDataManager)
				.getStockIdsByListDataProjectListId(StockServiceImplTest.TEST_LIST_ID);
	}

	@Test
	public void testCalculateNextStockIDPrefix() throws MiddlewareException {
		Mockito.when(this.inventoryService.getCurrentNotationNumberForBreederIdentifier(StockServiceImplTest.DUMMY_BREEDER_IDENTIFIER))
				.thenReturn(StockServiceImplTest.DUMMY_NOTATION_NUMBER);

		String prefix = this.inventoryStockService.calculateNextStockIDPrefix(StockServiceImplTest.DUMMY_BREEDER_IDENTIFIER, null);

		Assert.assertEquals(StockServiceImplTest.EXPECTED_PREFIX, prefix);
	}

	@Test
	public void testAssignStockIDs() throws MiddlewareException {
		Mockito.when(this.inventoryService.getCurrentNotationNumberForBreederIdentifier(StockServiceImplTest.DUMMY_BREEDER_IDENTIFIER))
				.thenReturn(StockServiceImplTest.DUMMY_NOTATION_NUMBER);

		// create a dummy inventory detail list
		List<InventoryDetails> inventoryDetailsList = new ArrayList<>();

		// we only populate the entry ID since it is what's relevant to the logic
		for (int i = 0; i < StockServiceImplTest.DUMMY_DETAIL_COUNT; i++) {
			InventoryDetails details = new InventoryDetails();
			details.setEntryId(i + 1);
			inventoryDetailsList.add(details);
		}

		this.inventoryStockService.assignStockIDs(inventoryDetailsList, StockServiceImplTest.DUMMY_BREEDER_IDENTIFIER, null);

		for (InventoryDetails details : inventoryDetailsList) {
			Assert.assertEquals(StockServiceImplTest.EXPECTED_PREFIX + details.getEntryId(), details.getInventoryID());
		}
	}

	@Test
	public void testVerifyIfBulkingForStockListCanProceed() throws Exception {
		List<InventoryDetails> inventoryDetails = this.createInventoryDetailsListTestData(true);
		this.inventoryStockService.verifyIfBulkingForStockListCanProceed(StockServiceImplTest.TEST_LIST_ID, inventoryDetails);
	}

	private List<String> createStockIdsTestData() {
		List<String> stockIDs = new ArrayList<String>();
		stockIDs.add(StockServiceImplTest.TEST_INVENTORY_ID);
		stockIDs.add(StockServiceImplTest.TEST_INVENTORY_ID_2);
		stockIDs.add(StockServiceImplTest.TEST_INVENTORY_ID_3);
		return stockIDs;
	}

	private List<InventoryDetails> createInventoryDetailsListTestData(boolean addBulkWith) {
		List<InventoryDetails> inventoryDetails = new ArrayList<InventoryDetails>();
		for (int i = 0; i < StockServiceImplTest.TEST_BULK_WITH_ARRAY.length; i++) {
			inventoryDetails.add(this.createInventoryDetailsTestData(i, addBulkWith));
		}
		return inventoryDetails;
	}

	private InventoryDetails createInventoryDetailsTestData(int id, boolean addBulkWith) {
		InventoryDetails inventoryDetails = new InventoryDetails();
		inventoryDetails.setTrnId(id);
		inventoryDetails.setLotId(id);
		inventoryDetails.setEntryId(id);
		inventoryDetails.setGid(id);
		inventoryDetails.setLotGid(id);
		inventoryDetails.setSourceRecordId(id * 10);
		inventoryDetails.setStockSourceRecordId(id * 10);
		inventoryDetails.setInventoryID(StockServiceImplTest.TEST_INVENTORY_ID_ARRAY[id]);
		inventoryDetails.setDuplicate(StockServiceImplTest.TEST_DUPLICATE_ARRAY[id]);
		if (addBulkWith) {
			inventoryDetails.setBulkWith(StockServiceImplTest.TEST_BULK_WITH_ARRAY[id]);
			if (inventoryDetails.getBulkWith() != null) {
				inventoryDetails.setBulkCompl("Y");
			}
		}
		return inventoryDetails;
	}

	@Test(expected = StockException.class)
	public void testVerifyIfBulkingForStockListCanProceed_NoBulkWith() throws Exception {
		List<InventoryDetails> inventoryDetails = this.createInventoryDetailsListTestData(false);
		this.inventoryStockService.verifyIfBulkingForStockListCanProceed(StockServiceImplTest.TEST_LIST_ID, inventoryDetails);
	}

	@Test(expected = StockException.class)
	public void testVerifyIfBulkingForStockListCanProceed_InvalidBulkWith() throws Exception {
		List<InventoryDetails> inventoryDetails = this.createInventoryDetailsListTestData(false);
		inventoryDetails.get(0).setBulkWith("any");
		this.inventoryStockService.verifyIfBulkingForStockListCanProceed(StockServiceImplTest.TEST_LIST_ID, inventoryDetails);
	}

	@Test(expected = StockException.class)
	public void testVerifyIfBulkingForStockListCanProceed_EntriesToBulkNotBulkComplete() throws Exception {
		List<InventoryDetails> inventoryDetails = this.createInventoryDetailsListTestData(true);
		inventoryDetails.get(0).setBulkCompl(null);
		this.inventoryStockService.verifyIfBulkingForStockListCanProceed(StockServiceImplTest.TEST_LIST_ID, inventoryDetails);
	}

	@Test(expected = StockException.class)
	public void testVerifyIfBulkingForStockListCanProceed_InvalidBulkCompl() throws Exception {
		List<InventoryDetails> inventoryDetails = this.createInventoryDetailsListTestData(true);
		inventoryDetails.get(0).setBulkCompl("wrong");
		this.inventoryStockService.verifyIfBulkingForStockListCanProceed(StockServiceImplTest.TEST_LIST_ID, inventoryDetails);
	}

	@Test(expected = StockException.class)
	public void testVerifyIfBulkingForStockListCanProceed_BulkComplButNullBulkWith() throws Exception {
		List<InventoryDetails> inventoryDetails = this.createInventoryDetailsListTestData(true);
		inventoryDetails.get(0).setBulkWith(null);
		this.inventoryStockService.verifyIfBulkingForStockListCanProceed(StockServiceImplTest.TEST_LIST_ID, inventoryDetails);
	}

	@Test
	public void testGetBulkingDonors() throws MiddlewareException {
		List<InventoryDetails> inventoryDetailsList = this.createInventoryDetailsListTestData(true);
		Map<String, Integer> stockIDEntryMap = new HashMap<String, Integer>();
		Map<Integer, InventoryDetails> entryInventoryDetailsMap = new HashMap<Integer, InventoryDetails>();
		this.inventoryStockService.buildMapsOfInventoryDetails(inventoryDetailsList, stockIDEntryMap, entryInventoryDetailsMap);
		List<InventoryDetails> bulkingDonors = new ArrayList<InventoryDetails>();
		List<InventoryDetails> bulkingRecipients = new ArrayList<InventoryDetails>();
		this.inventoryStockService.retrieveBulkingDonorsAndRecipients(inventoryDetailsList, stockIDEntryMap, entryInventoryDetailsMap,
				bulkingDonors, bulkingRecipients);
		for (InventoryDetails inventoryDetails : bulkingDonors) {
			switch (inventoryDetails.getTrnId()) {
				case 0:
				case 1:
				case 2:
					Assert.assertEquals(0, inventoryDetails.getSourceRecordId().intValue());
					break;
				default:
					break;
			}
			switch (inventoryDetails.getLotId()) {
				case 0:
				case 1:
				case 2:
					Assert.assertEquals(0, inventoryDetails.getLotGid().intValue());
					break;
				default:
					break;
			}
			Assert.assertEquals(InventoryDetails.BULK_COMPL_COMPLETED, inventoryDetails.getBulkCompl());
		}
		for (InventoryDetails inventoryDetails : bulkingRecipients) {
			Assert.assertEquals(InventoryDetails.BULK_COMPL_COMPLETED, inventoryDetails.getBulkCompl());
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testExecuteBulkingInstructions() throws MiddlewareException {
		StockServiceImpl spy = Mockito.spy(this.inventoryStockService);
		this.mockReturnedTransactions();
		this.mockReturnedLots();
		this.mockSaveChangesToBulkingDonors(spy);
		List<InventoryDetails> inventoryDetailsList = this.createInventoryDetailsListTestData(true);
		spy.executeBulkingInstructions(inventoryDetailsList);
		Mockito.verify(spy).saveChangesInBulkingProcess(Matchers.anyList(), Matchers.anyList());
	}

	@SuppressWarnings("unchecked")
	private void mockSaveChangesToBulkingDonors(StockServiceImpl spy) throws MiddlewareQueryException {
		Mockito.doNothing().when(spy).saveChangesInBulkingProcess(Matchers.anyList(), Matchers.anyList());
	}

	private void mockReturnedLots() throws MiddlewareQueryException {
		for (int i = 0; i < StockServiceImplTest.TEST_BULK_WITH_ARRAY.length; i++) {
			Mockito.doReturn(new Lot(i)).when(this.inventoryDataManager).getLotById(i);
		}
	}

	private void mockReturnedTransactions() throws MiddlewareQueryException {
		for (int i = 0; i < StockServiceImplTest.TEST_BULK_WITH_ARRAY.length; i++) {
			Mockito.doReturn(new Transaction(i)).when(this.inventoryDataManager).getTransactionById(i);
		}
	}

	@Test
	public void testProcessBulkWithNoInstructions() {
		List<ListDataProject> testListData = this.createTestListDataProjectForBulking();
		Map<Integer, InventoryDetails> testDetails = this.createInventoryDetailsTestData();

		this.inventoryStockService.processBulkSettings(testListData, testDetails, false, false, false);

		for (InventoryDetails inventoryDetails : testDetails.values()) {
			Assert.assertNull(inventoryDetails.getBulkWith());
			Assert.assertNull(inventoryDetails.getBulkCompl());
		}
	}

	@Test
	public void testProcessBulkWithPedigreeDupe() {
		List<ListDataProject> testListData = this.createTestListDataProjectForBulking();
		Map<Integer, InventoryDetails> testDetails = this.createInventoryDetailsTestData();

		this.inventoryStockService.processBulkSettings(testListData, testDetails, true, false, false);

		for (InventoryDetails inventoryDetails : testDetails.values()) {
			if (inventoryDetails.getEntryId().equals(9)) {
				Assert.assertEquals(StockServiceImplTest.INVENTORY_ID_PREFIX + 10, inventoryDetails.getBulkWith());
				Assert.assertEquals("Y", inventoryDetails.getBulkCompl());
			} else if (inventoryDetails.getEntryId().equals(10)) {
				Assert.assertEquals(StockServiceImplTest.INVENTORY_ID_PREFIX + 9, inventoryDetails.getBulkWith());
				Assert.assertEquals("Y", inventoryDetails.getBulkCompl());
			} else {
				Assert.assertNull(inventoryDetails.getBulkWith());
				Assert.assertNull(inventoryDetails.getBulkCompl());
			}

		}
	}

	@Test
	public void testProcessBulkWithEntriesBothPlotDupeAndPlotReciprocal() {
		List<ListDataProject> testListData = this.createTestListDataProjectForBulking();
		Map<Integer, InventoryDetails> testDetails = this.createInventoryDetailsTestData();

		this.inventoryStockService.processBulkSettings(testListData, testDetails, false, true, false);
		for (InventoryDetails inventoryDetails : testDetails.values()) {
			if (inventoryDetails.getEntryId().equals(1)) {
				String expectedBulkWith = StockServiceImplTest.INVENTORY_ID_PREFIX + 2 + ", " + StockServiceImplTest.INVENTORY_ID_PREFIX
						+ 20 + ", " + StockServiceImplTest.INVENTORY_ID_PREFIX + 4;
				Assert.assertEquals(expectedBulkWith, inventoryDetails.getBulkWith());
				Assert.assertEquals("Y", inventoryDetails.getBulkCompl());
			} else if (inventoryDetails.getEntryId().equals(2)) {
				String expectedBulkWith = StockServiceImplTest.INVENTORY_ID_PREFIX + 1 + ", " + StockServiceImplTest.INVENTORY_ID_PREFIX
						+ 20 + ", " + StockServiceImplTest.INVENTORY_ID_PREFIX + 4;
				Assert.assertEquals(expectedBulkWith, inventoryDetails.getBulkWith());
				Assert.assertEquals("Y", inventoryDetails.getBulkCompl());
			} else if (inventoryDetails.getEntryId().equals(4)) {
				String expectedBulkWith = StockServiceImplTest.INVENTORY_ID_PREFIX + 1 + ", " + StockServiceImplTest.INVENTORY_ID_PREFIX + 2
						+ ", " + StockServiceImplTest.INVENTORY_ID_PREFIX + 20;
				Assert.assertEquals(expectedBulkWith, inventoryDetails.getBulkWith());
				Assert.assertEquals("Y", inventoryDetails.getBulkCompl());
			} else if (inventoryDetails.getEntryId().equals(20)) {
				InventoryDetails dummy = new InventoryDetails();
				dummy.addBulkWith(testDetails.get(1).getInventoryID());
				dummy.addBulkWith(testDetails.get(2).getInventoryID());
				dummy.addBulkWith(testDetails.get(4).getInventoryID());
				Assert.assertEquals(dummy.getBulkWith(), inventoryDetails.getBulkWith());
				Assert.assertEquals("Y", inventoryDetails.getBulkCompl());
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

		ldp = new ListDataProject();
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

		this.addInventoryDetailTestData(1, detailMap);
		this.addInventoryDetailTestData(2, detailMap);
		this.addInventoryDetailTestData(3, detailMap);
		this.addInventoryDetailTestData(4, detailMap);
		this.addInventoryDetailTestData(5, detailMap);
		this.addInventoryDetailTestData(6, detailMap);
		this.addInventoryDetailTestData(7, detailMap);
		this.addInventoryDetailTestData(8, detailMap);
		this.addInventoryDetailTestData(9, detailMap);
		this.addInventoryDetailTestData(10, detailMap);
		this.addInventoryDetailTestData(11, detailMap);
		this.addInventoryDetailTestData(20, detailMap);

		return detailMap;
	}

	protected void addInventoryDetailTestData(Integer entryId, Map<Integer, InventoryDetails> detailMap) {
		InventoryDetails details = new InventoryDetails();
		details.setEntryId(entryId);
		details.setInventoryID(StockServiceImplTest.INVENTORY_ID_PREFIX + entryId);
		detailMap.put(entryId, details);
	}

}
