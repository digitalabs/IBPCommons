package org.generationcp.commons.service;

import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.exceptions.MiddlewareException;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 */

public interface InventoryStockService {
	void assignStockIDs(List<InventoryDetails> details) throws MiddlewareException;

	void assignStockIDs(List<InventoryDetails> details,
			String breederIdentifier, String separator) throws MiddlewareException;

	String calculateNextStockIDPrefix(String breederIdentifier, String separator) throws MiddlewareException;
}
