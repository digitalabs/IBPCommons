
package org.generationcp.commons.service;

import java.util.List;

import org.generationcp.commons.exceptions.StockException;
import org.generationcp.middleware.domain.inventory.InventoryDetails;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte
 */

public interface StockService {

	String calculateNextStockIDPrefix(String breederIdentifier, String separator);

}
