package org.generationcp.commons.service.impl;

import org.generationcp.commons.ruleengine.RuleException;
import org.generationcp.commons.ruleengine.RuleFactory;
import org.generationcp.commons.ruleengine.service.RulesService;
import org.generationcp.commons.ruleengine.stockid.StockIDGenerationRuleExecutionContext;
import org.generationcp.commons.service.StockService;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.service.api.InventoryService;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 * Date: 4/16/2015
 * Time: 2:51 PM
 */
public class StockServiceImpl implements StockService {

	@Resource
	private RulesService rulesService;

	@Resource
	private RuleFactory ruleFactory;

	@Resource
	private InventoryService inventoryService;

	@Override public void assignStockIDs(List<InventoryDetails> details)
			throws MiddlewareException {

		this.assignStockIDs(details, null, null);
	}

	@Override public void assignStockIDs(List<InventoryDetails> details, String breederIdentifier,
			String separator)
			throws MiddlewareException {
		String stockIDPrefix = calculateNextStockIDPrefix(breederIdentifier, separator);

		for (InventoryDetails detail : details) {
			detail.setInventoryID(stockIDPrefix + detail.getEntryId());
		}
	}

	/**
	 *
	 * Returns the stock ID prefix (consisting of breeder identifier plus current notation number) and the configured separator
	 * e.g., 'DV1-'
	 * @param breederIdentifier
	 * @param separator
	 * @return
	 * @throws MiddlewareException
	 */
	@Override
	public String calculateNextStockIDPrefix(String breederIdentifier, String separator) throws
			MiddlewareException {
		List<String> sequenceList = Arrays
				.asList(ruleFactory.getRuleSequenceForNamespace("stockid"));
		StockIDGenerationRuleExecutionContext context = new StockIDGenerationRuleExecutionContext(
				sequenceList, inventoryService);
		context.setBreederIdentifier(breederIdentifier);
		context.setSeparator(separator);

		try {
			rulesService.runRules(context);
			return (String) context.getRuleExecutionOutput();

		} catch (RuleException e) {
			throw new MiddlewareException(e.getMessage(), e);
		}
	}

	@Override
	public void processBulkSettings(Collection<ListDataProject> dataProjectList, Map<Integer, InventoryDetails> inventoryDetailsMap,
									boolean addPedigreeDuplicate, boolean addPlotReciprocal, boolean addPedigreeReciprocal) {
		for (ListDataProject listDataProject : dataProjectList) {
			if (addPedigreeDuplicate) {
				processBulkWith(listDataProject.getEntryId(), listDataProject.parsePedigreeDupeInformation(), inventoryDetailsMap);
			}

			if (addPlotReciprocal) {
				processBulkWith(listDataProject.getEntryId(), listDataProject.parsePlotReciprocalInformation(), inventoryDetailsMap);
			}

			if (addPedigreeReciprocal) {
				processBulkWith(listDataProject.getEntryId(), listDataProject.parsePedigreeReciprocalInformation(), inventoryDetailsMap);
			}
		}
	}

	protected void processBulkWith(Integer targetEntryID, List<Integer> entryIds, Map<Integer, InventoryDetails> detailMap) {
		InventoryDetails target = detailMap.get(targetEntryID);

		if (entryIds.size() == 0) {
			return;
		}

		target.setBulkCompl("Y");

		for (Integer entryId : entryIds) {
			InventoryDetails bulkDetail = detailMap.get(entryId);
			if (bulkDetail == null) {
				continue;
			}

			target.addBulkWith(bulkDetail.getInventoryID());
		}
	}
}