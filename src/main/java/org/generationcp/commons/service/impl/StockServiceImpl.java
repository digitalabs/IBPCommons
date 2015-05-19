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
import java.util.*;

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

    @Override
    public void assignStockIDs(List<InventoryDetails> details)
            throws MiddlewareException {

        this.assignStockIDs(details, null, null);
    }

    @Override
    public void assignStockIDs(List<InventoryDetails> details, String breederIdentifier,
                               String separator)
            throws MiddlewareException {
        String stockIDPrefix = calculateNextStockIDPrefix(breederIdentifier, separator);

        for (InventoryDetails detail : details) {
            detail.setInventoryID(stockIDPrefix + detail.getEntryId());
        }
    }

    /**
     * Returns the stock ID prefix (consisting of breeder identifier plus current notation number) and the configured separator
     * e.g., 'DV1-'
     *
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

        Map<Integer, ListDataProject> projectMap = new HashMap<>();

        for (ListDataProject project : dataProjectList) {
            projectMap.put(project.getEntryId(), project);
        }

        if (addPedigreeDuplicate) {
            processBulkWith(projectMap, new RetrieveParseIDStrategy() {
                @Override
                public List<Integer> retrieveParsedIDsForProcessing(ListDataProject project) {
                    return project.parsePedigreeDupeInformation();
                }
            }, inventoryDetailsMap);
        }

        if (addPlotReciprocal) {
            processBulkWith(projectMap, new RetrieveParseIDStrategy() {
                @Override
                public List<Integer> retrieveParsedIDsForProcessing(ListDataProject project) {
                    return project.parsePlotReciprocalInformation();
                }
            }, inventoryDetailsMap);
        }

        if (addPedigreeReciprocal) {
            processBulkWith(projectMap, new RetrieveParseIDStrategy() {
                @Override
                public List<Integer> retrieveParsedIDsForProcessing(ListDataProject project) {
                    return project.parsePedigreeReciprocalInformation();
                }
            }, inventoryDetailsMap);
        }

    }

    protected void processBulkWith(Map<Integer, ListDataProject> projectMap, RetrieveParseIDStrategy strategy,
                                   Map<Integer, InventoryDetails> detailMap) {
        List<Integer> processedEntries = new ArrayList<>();

        for (ListDataProject project : projectMap.values()) {
            List<Integer> parsedIDs = strategy.retrieveParsedIDsForProcessing(project);

            if (parsedIDs.isEmpty()) {
                continue;
            }

            processEntry(project.getEntryId(), projectMap, strategy, detailMap, processedEntries);
        }

    }

    protected void processEntry(Integer targetEntryID, Map<Integer, ListDataProject> projectMap, RetrieveParseIDStrategy strategy,
                                Map<Integer, InventoryDetails> detailMap, List<Integer> processedEntries) {

        if( processedEntries.contains(targetEntryID)) {
            return;
        }

        List<Integer> parsedIDs = strategy.retrieveParsedIDsForProcessing(projectMap.get(targetEntryID));

        InventoryDetails target = detailMap.get(targetEntryID);

        target.setBulkCompl("Y");
        processedEntries.add(targetEntryID);
        List<String> targetInventoryIDs = new ArrayList<>();
        for (Integer parsedId : parsedIDs) {

            InventoryDetails bulkDetail = detailMap.get(parsedId);

            if (bulkDetail == null) {
                continue;
            }

            target.addBulkWith(bulkDetail.getInventoryID());
            targetInventoryIDs.add(bulkDetail.getInventoryID());
            bulkDetail.addBulkWith(target.getInventoryID());
            processEntry(parsedId, projectMap, strategy, detailMap, processedEntries);
        }

        // this second pass ensures that all of the inventory IDs participating in a bulk with operation is applied to all applicable entries
        for (Integer parsedID : parsedIDs) {
            InventoryDetails bulkDetail = detailMap.get(parsedID);

            for (String targetInventoryID : targetInventoryIDs) {
                if (!bulkDetail.getInventoryID().equals(targetInventoryID)) {
                    bulkDetail.addBulkWith(targetInventoryID);
                }

            }
        }

    }

    interface RetrieveParseIDStrategy {
        List<Integer> retrieveParsedIDsForProcessing(ListDataProject project);
    }
}