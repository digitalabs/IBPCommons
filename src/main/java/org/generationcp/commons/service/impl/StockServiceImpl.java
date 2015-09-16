
package org.generationcp.commons.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.exceptions.StockException;
import org.generationcp.commons.parsing.validation.BulkComplValidator;
import org.generationcp.commons.parsing.validation.CommaDelimitedValueValidator;
import org.generationcp.commons.ruleengine.RuleException;
import org.generationcp.commons.ruleengine.RuleFactory;
import org.generationcp.commons.ruleengine.service.RulesService;
import org.generationcp.commons.ruleengine.stockid.StockIDGenerationRuleExecutionContext;
import org.generationcp.commons.service.StockService;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.service.api.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 4/16/2015 Time: 2:51 PM
 */
@Transactional
public class StockServiceImpl implements StockService {

	private static final Logger LOG = LoggerFactory.getLogger(StockServiceImpl.class);

	@Resource
	private RulesService rulesService;

	@Resource
	private RuleFactory ruleFactory;

	@Resource
	private InventoryService inventoryService;

	@Resource
	private InventoryDataManager inventoryDataManager;

	@Override
	public void assignStockIDs(List<InventoryDetails> details) {

		this.assignStockIDs(details, null, null);
	}

	@Override
	public void assignStockIDs(List<InventoryDetails> details, String breederIdentifier, String separator) {
		String stockIDPrefix = this.calculateNextStockIDPrefix(breederIdentifier, separator);

		for (InventoryDetails detail : details) {
			detail.setInventoryID(stockIDPrefix + detail.getEntryId());
		}
	}

	/**
	 * Returns the stock ID prefix (consisting of breeder identifier plus current notation number) and the configured separator e.g., 'DV1-'
	 *
	 * @param breederIdentifier
	 * @param separator
	 * @return
	 * @throws MiddlewareException
	 */
	@Override
	public String calculateNextStockIDPrefix(String breederIdentifier, String separator) {
		List<String> sequenceList = Arrays.asList(this.ruleFactory.getRuleSequenceForNamespace("stockid"));
		StockIDGenerationRuleExecutionContext context = new StockIDGenerationRuleExecutionContext(sequenceList, this.inventoryService);
		context.setBreederIdentifier(breederIdentifier);
		context.setSeparator(separator);

		try {
			this.rulesService.runRules(context);
			return (String) context.getRuleExecutionOutput();

		} catch (RuleException e) {
			throw new MiddlewareException(e.getMessage(), e);
		}
	}

	@Override
	public void processBulkSettings(Collection<ListDataProject> dataProjectList, Map<Integer, InventoryDetails> inventoryDetailsMap,
			boolean addPedigreeDuplicate, boolean addPlotReciprocal, boolean addPedigreeReciprocal) {

		Map<Integer, ListDataProject> projectMap = new HashMap<>();

		for (ListDataProject project : dataProjectList) {
			projectMap.put(project.getEntryId(), project);
		}

		if (addPedigreeDuplicate) {
			this.processBulkWith(projectMap, new RetrieveParseIDStrategy() {

				@Override
				public List<Integer> retrieveParsedIDsForProcessing(ListDataProject project) {
					return project.parsePedigreeDupeInformation();
				}
			}, inventoryDetailsMap);
		}

		if (addPlotReciprocal) {
			this.processBulkWith(projectMap, new RetrieveParseIDStrategy() {

				@Override
				public List<Integer> retrieveParsedIDsForProcessing(ListDataProject project) {
					return project.parsePlotReciprocalInformation();
				}
			}, inventoryDetailsMap);
		}

		if (addPedigreeReciprocal) {
			this.processBulkWith(projectMap, new RetrieveParseIDStrategy() {

				@Override
				public List<Integer> retrieveParsedIDsForProcessing(ListDataProject project) {
					return project.parsePedigreeReciprocalInformation();
				}
			}, inventoryDetailsMap);
		}

		this.ensureBulkWithIsConsistentInAllParticipants(inventoryDetailsMap);

	}

	protected void processBulkWith(Map<Integer, ListDataProject> projectMap, RetrieveParseIDStrategy strategy,
			Map<Integer, InventoryDetails> detailsMap) {

		for (ListDataProject project : projectMap.values()) {
			List<Integer> parsedIDs = strategy.retrieveParsedIDsForProcessing(project);

			if (parsedIDs.isEmpty()) {
				continue;
			}

			this.processEntry(project.getEntryId(), projectMap, strategy, detailsMap);
		}

	}

	protected void processEntry(Integer targetEntryID, Map<Integer, ListDataProject> projectMap, RetrieveParseIDStrategy strategy,
			Map<Integer, InventoryDetails> detailMap) {

		List<Integer> parsedIDs = strategy.retrieveParsedIDsForProcessing(projectMap.get(targetEntryID));

		InventoryDetails target = detailMap.get(targetEntryID);

		target.setBulkCompl("Y");
		for (Integer parsedId : parsedIDs) {

			InventoryDetails bulkDetail = detailMap.get(parsedId);

			if (bulkDetail == null) {
				continue;
			}

			target.addBulkWith(bulkDetail.getInventoryID());
			bulkDetail.addBulkWith(target.getInventoryID());
		}

	}

	protected void ensureBulkWithIsConsistentInAllParticipants(Map<Integer, InventoryDetails> entryNoInventoryDetailsMap) {

		Map<String, InventoryDetails> inventoryIDInventoryDetailsMap = new HashMap<>();
		for (InventoryDetails inventoryDetails : entryNoInventoryDetailsMap.values()) {
			inventoryIDInventoryDetailsMap.put(inventoryDetails.getInventoryID(), inventoryDetails);
		}

		List<String> processedStockIds = new ArrayList<>();
		for (InventoryDetails target : entryNoInventoryDetailsMap.values()) {
			if (target.getBulkWith() != null && !processedStockIds.contains(target.getInventoryID())) {
				Set<String> stockIdsToBulkTogether = this.getAllStockIdsToBulkTogether(target, inventoryIDInventoryDetailsMap);
				processedStockIds.addAll(processedStockIds);
				for (String stockId : stockIdsToBulkTogether) {
					InventoryDetails inventory = inventoryIDInventoryDetailsMap.get(stockId);
					inventory.setBulkWith(null);
					inventory.setBulkCompl("Y");
					this.setBulkWithOfInventory(inventory, stockIdsToBulkTogether);
				}
			}
		}
	}

	private void setBulkWithOfInventory(InventoryDetails inventory, Set<String> stockIdsToBulkTogether) {
		for (String stockIdToBulkWith : stockIdsToBulkTogether) {
			if (!inventory.getInventoryID().equals(stockIdToBulkWith)) {
				inventory.addBulkWith(stockIdToBulkWith);
			}
		}
	}

	private Set<String> getAllStockIdsToBulkTogether(InventoryDetails target,
			Map<String, InventoryDetails> inventoryIDInventoryDetailsMap) {
		Set<String> stockIdsToBulkTogether = new TreeSet<>();
		stockIdsToBulkTogether.add(target.getInventoryID());
		for (String stockId : target.getBulkWithStockIds()) {
			stockIdsToBulkTogether.add(stockId);
			InventoryDetails inventoryCoBulkWith = inventoryIDInventoryDetailsMap.get(stockId);
			for (String bulkWithOfCoBulkWith : inventoryCoBulkWith.getBulkWithStockIds()) {
				stockIdsToBulkTogether.add(bulkWithOfCoBulkWith);
			}
		}
		return stockIdsToBulkTogether;
	}

	interface RetrieveParseIDStrategy {

		List<Integer> retrieveParsedIDsForProcessing(ListDataProject project);
	}

	@Override
	public void verifyIfBulkingForStockListCanProceed(Integer listId, List<InventoryDetails> inventoryDetailsList) throws StockException {
		this.validateBulkWithValues(listId, inventoryDetailsList);
		this.validateBulkComplValues(inventoryDetailsList);
	}

	protected void validateBulkWithValues(Integer listId, List<InventoryDetails> inventoryDetailsList) throws StockException {
		List<String> bulkWithValues = this.hasAtLeastOneBulkWithValue(inventoryDetailsList);
		this.containsValidBulkWithValues(listId, bulkWithValues);
	}

	private List<String> hasAtLeastOneBulkWithValue(List<InventoryDetails> inventoryDetailsList) throws StockException {
		List<String> bulkWithValues = this.retrieveAllBulkWithValues(inventoryDetailsList);
		if (bulkWithValues == null || bulkWithValues.isEmpty()) {
			throw new StockException("bulking.instruction.bulk.with.required.one.entry");
		}
		return bulkWithValues;
	}

	protected List<String> retrieveAllBulkWithValues(List<InventoryDetails> inventoryDetailsList) {
		List<String> bulkWithValues = new ArrayList<String>();
		for (InventoryDetails inventoryDetails : inventoryDetailsList) {
			if (!StringUtils.isEmpty(inventoryDetails.getBulkWith())) {
				bulkWithValues.add(inventoryDetails.getBulkWith());
			}
		}
		return bulkWithValues;
	}

	private void containsValidBulkWithValues(Integer listId, List<String> bulkWithValues) throws StockException {
		CommaDelimitedValueValidator bulkWithValidator = new CommaDelimitedValueValidator(this.getStockIdsOfStockList(listId));
		for (String bulkWith : bulkWithValues) {
			if (!bulkWithValidator.isParsedValueValid(bulkWith, null)) {
				throw new StockException("bulking.instruction.bulk.with.invalid.stock.id");
			}
		}
	}

	public List<String> getStockIdsOfStockList(Integer listId) {
		List<String> stockIDList = new ArrayList<>();
		try {
			stockIDList = this.inventoryDataManager.getStockIdsByListDataProjectListId(listId);
		} catch (MiddlewareQueryException e) {
			StockServiceImpl.LOG.error(e.getMessage(), e);
		}
		return stockIDList;
	}

	protected void validateBulkComplValues(List<InventoryDetails> inventoryDetailsList) throws StockException {
		BulkComplValidator bulkComplValidator = new BulkComplValidator(true);
		Map<String, InventoryDetails> stockIDInventoryDetailsMap = this.getStockIDInventoryDetailsMap(inventoryDetailsList);
		for (InventoryDetails inventoryDetails : inventoryDetailsList) {
			if (!bulkComplValidator.isParsedValueValid(inventoryDetails.getBulkCompl(), inventoryDetails.getBulkWith())) {
				throw new StockException("bulking.instruction.bulk.compl.invalid.value");
			}
			if (!StringUtils.isEmpty(inventoryDetails.getBulkCompl())) {
				this.verifyIfAllEntriesToBeMergedAreBulkComplete(inventoryDetails, stockIDInventoryDetailsMap);
			}
		}
	}

	private void verifyIfAllEntriesToBeMergedAreBulkComplete(InventoryDetails inventoryDetails,
			Map<String, InventoryDetails> stockIDInventoryDetailsMap) throws StockException {
		String bulkWith = inventoryDetails.getBulkWith();
		String[] stockIDsTobeMergedWith = bulkWith.split(",");
		for (String stockID : stockIDsTobeMergedWith) {
			InventoryDetails inventoryDetailsToBeMergedWith = stockIDInventoryDetailsMap.get(stockID.trim());
			if (inventoryDetailsToBeMergedWith == null || !"Y".equals(inventoryDetailsToBeMergedWith.getBulkCompl())) {
				throw new StockException("bulking.instruction.bulk.compl.invalid.value");
			}
		}

	}

	private Map<String, InventoryDetails> getStockIDInventoryDetailsMap(List<InventoryDetails> inventoryDetailsList) {
		Map<String, InventoryDetails> stockIDInventoryDetailsMap = new HashMap<String, InventoryDetails>();
		for (InventoryDetails inventoryDetails : inventoryDetailsList) {
			stockIDInventoryDetailsMap.put(inventoryDetails.getInventoryID(), inventoryDetails);
		}
		return stockIDInventoryDetailsMap;
	}

	@Override
	public void executeBulkingInstructions(List<InventoryDetails> inventoryDetailsList) {
		Map<String, Integer> stockIDEntryMap = new HashMap<String, Integer>();
		Map<Integer, InventoryDetails> entryInventoryDetailsMap = new HashMap<Integer, InventoryDetails>();
		this.buildMapsOfInventoryDetails(inventoryDetailsList, stockIDEntryMap, entryInventoryDetailsMap);

		List<InventoryDetails> bulkingDonors = new ArrayList<InventoryDetails>();
		List<InventoryDetails> bulkingRecipients = new ArrayList<InventoryDetails>();
		this.retrieveBulkingDonorsAndRecipients(inventoryDetailsList, stockIDEntryMap, entryInventoryDetailsMap, bulkingDonors,
				bulkingRecipients);
		this.saveChangesInBulkingProcess(bulkingDonors, bulkingRecipients);
	}

	protected void saveChangesInBulkingProcess(List<InventoryDetails> bulkingDonors, List<InventoryDetails> bulkingRecipients) {
		List<Transaction> transactions = new ArrayList<Transaction>();
		List<Lot> lots = new ArrayList<Lot>();
		if (bulkingDonors != null && bulkingRecipients != null) {
			this.collectBulkedInventoryAsLotAndTransaction(bulkingDonors, true, transactions, lots);
			this.collectBulkedInventoryAsLotAndTransaction(bulkingRecipients, false, transactions, lots);
			this.inventoryDataManager.updateTransactions(transactions);
			this.inventoryDataManager.updateLots(lots);
		}
	}

	private void collectBulkedInventoryAsLotAndTransaction(List<InventoryDetails> inventoryDetailsList, boolean isDonor,
			List<Transaction> transactions, List<Lot> lots) {
		for (InventoryDetails inventoryDetails : inventoryDetailsList) {
			Transaction transaction = this.getTransactionById(inventoryDetails.getTrnId());
			if (isDonor) {
				transaction.setSourceRecordId(inventoryDetails.getSourceRecordId());
				Lot lot = this.getLotById(inventoryDetails.getLotId());
				lot.setEntityId(inventoryDetails.getLotGid());
				lots.add(lot);
			}
			transaction.setBulkCompl(inventoryDetails.getBulkCompl());
			transactions.add(transaction);
		}
	}

	protected void buildMapsOfInventoryDetails(List<InventoryDetails> inventoryDetailsList, Map<String, Integer> stockIDEntryMap,
			Map<Integer, InventoryDetails> entryInventoryDetailsMap) {
		for (InventoryDetails inventoryDetails : inventoryDetailsList) {
			stockIDEntryMap.put(inventoryDetails.getInventoryID(), inventoryDetails.getEntryId());
			entryInventoryDetailsMap.put(inventoryDetails.getEntryId(), inventoryDetails);
		}
	}

	protected void retrieveBulkingDonorsAndRecipients(List<InventoryDetails> inventoryDetailsList, Map<String, Integer> stockIDEntryMap,
			Map<Integer, InventoryDetails> entryInventoryDetailsMap, List<InventoryDetails> bulkingDonors,
			List<InventoryDetails> bulkingRecipients) {
		for (InventoryDetails inventoryDetails : inventoryDetailsList) {
			if (InventoryDetails.BULK_COMPL_Y.equals(inventoryDetails.getBulkCompl())) {
				Set<Integer> entriesToBeMerged = new TreeSet<Integer>();
				String[] stockIDs = inventoryDetails.getBulkWith().split(",");
				for (String stockID : stockIDs) {
					entriesToBeMerged.add(stockIDEntryMap.get(stockID.trim()));
				}
				entriesToBeMerged.add(stockIDEntryMap.get(inventoryDetails.getInventoryID()));
				Integer recipientsEntry = entriesToBeMerged.iterator().next();
				if (!inventoryDetails.getEntryId().equals(recipientsEntry)) {
					// it is a donor
					InventoryDetails recipientsInventoryDetails = entryInventoryDetailsMap.get(recipientsEntry);
					inventoryDetails.setSourceRecordId(recipientsInventoryDetails.getSourceRecordId());
					inventoryDetails.setLotGid(recipientsInventoryDetails.getGid());
					bulkingDonors.add(inventoryDetails);
				} else {
					// it is a recipient
					bulkingRecipients.add(inventoryDetails);
				}
				inventoryDetails.setBulkCompl(InventoryDetails.BULK_COMPL_COMPLETED);
			}
		}
	}

	private Transaction getTransactionById(Integer trnId) {
		return this.inventoryDataManager.getTransactionById(trnId);
	}

	private Lot getLotById(Integer lotId) {
		return this.inventoryDataManager.getLotById(lotId);
	}
}
