package org.generationcp.commons.service;

import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.pojos.ListDataProject;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 */

public interface StockService {
	void assignStockIDs(List<InventoryDetails> details) throws MiddlewareException;

	void assignStockIDs(List<InventoryDetails> details,
			String breederIdentifier, String separator) throws MiddlewareException;

	String calculateNextStockIDPrefix(String breederIdentifier, String separator) throws MiddlewareException;

	void processBulkSettings(List<ListDataProject> dataProjectList, Map<Integer, InventoryDetails> inventoryDetailsMap, boolean addPedigreeDuplicate, boolean  addPlotReciprocal, boolean addPedigreeReciprocal);

}
