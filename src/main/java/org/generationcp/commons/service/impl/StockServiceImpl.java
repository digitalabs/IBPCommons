
package org.generationcp.commons.service.impl;

import org.generationcp.commons.ruleengine.RuleException;
import org.generationcp.commons.ruleengine.RuleFactory;
import org.generationcp.commons.ruleengine.service.RulesService;
import org.generationcp.commons.ruleengine.stockid.StockIDGenerationRuleExecutionContext;
import org.generationcp.commons.service.StockService;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.service.api.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * This class is used for providing stock-related functionalities such as assigning stock IDs, processing bulk settings and bulking
 * duplicates and reciprocals
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
	public void executeBulkingInstructions(List<InventoryDetails> inventoryDetailsList) {
		Map<String, Integer> stockIDEntryMap = new HashMap<>();
		Map<Integer, InventoryDetails> entryInventoryDetailsMap = new HashMap<>();
		this.buildMapsOfInventoryDetails(inventoryDetailsList, stockIDEntryMap, entryInventoryDetailsMap);

		List<InventoryDetails> bulkingDonors = new ArrayList<>();
		List<InventoryDetails> bulkingRecipients = new ArrayList<>();
		this.retrieveBulkingDonorsAndRecipients(inventoryDetailsList, stockIDEntryMap, entryInventoryDetailsMap, bulkingDonors,
				bulkingRecipients);
		this.saveChangesInBulkingProcess(bulkingDonors, bulkingRecipients);
	}

	protected void saveChangesInBulkingProcess(List<InventoryDetails> bulkingDonors, List<InventoryDetails> bulkingRecipients) {
		List<Transaction> transactions = new ArrayList<>();
		List<Lot> lots = new ArrayList<>();
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
				Set<Integer> entriesToBeMerged = new TreeSet<>();
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
