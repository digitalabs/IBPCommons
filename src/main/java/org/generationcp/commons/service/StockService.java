
package org.generationcp.commons.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.exceptions.StockException;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.pojos.ListDataProject;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte
 */

public interface StockService {

	void assignStockIDs(List<InventoryDetails> details) throws MiddlewareException;

	void assignStockIDs(List<InventoryDetails> details, String breederIdentifier, String separator) throws MiddlewareException;

	String calculateNextStockIDPrefix(String breederIdentifier, String separator) throws MiddlewareException;

	void processBulkSettings(Collection<ListDataProject> dataProjectList, Map<Integer, InventoryDetails> inventoryDetailsMap,
			boolean addPedigreeDuplicate, boolean addPlotReciprocal, boolean addPedigreeReciprocal);

	void verifyIfBulkingForStockListCanProceed(Integer listId, List<InventoryDetails> inventoryDetailsList) throws StockException;

	void executeBulkingInstructions(List<InventoryDetails> inventoryDetailsList) throws MiddlewareException;
}
