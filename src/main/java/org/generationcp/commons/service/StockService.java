
package org.generationcp.commons.service;

import java.util.List;

import org.generationcp.commons.exceptions.StockException;
import org.generationcp.middleware.domain.inventory.InventoryDetails;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte
 */

public interface StockService {

	void assignStockIDs(List<InventoryDetails> details);

	void assignStockIDs(List<InventoryDetails> details, String breederIdentifier, String separator);

	String calculateNextStockIDPrefix(String breederIdentifier, String separator);

	void executeBulkingInstructions(List<InventoryDetails> inventoryDetailsList);
}
