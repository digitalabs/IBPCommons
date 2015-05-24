package org.generationcp.commons.ruleengine.stockid;

import org.generationcp.commons.ruleengine.OrderedRuleExecutionContext;
import org.generationcp.middleware.service.api.InventoryService;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 */
public class StockIDGenerationRuleExecutionContext extends OrderedRuleExecutionContext {

	private InventoryService inventoryService;
	private StringBuilder stockIDGenerationBuilder;
	private String breederIdentifier;
	private Integer notationNumber;
	private String separator;
	private Long sequenceNumber;

	public StockIDGenerationRuleExecutionContext(List<String> executionOrder) {
		super(executionOrder);
	}

	public StockIDGenerationRuleExecutionContext(List<String> executionOrder,
			InventoryService inventoryService) {
		super(executionOrder);
		this.inventoryService = inventoryService;
		stockIDGenerationBuilder = new StringBuilder();
	}

	@Override public Object getRuleExecutionOutput() {
		return stockIDGenerationBuilder.toString();
	}

	public StringBuilder getStockIDGenerationBuilder() {
		return stockIDGenerationBuilder;
	}

	public void setStockIDGenerationBuilder(StringBuilder stockIDGenerationBuilder) {
		this.stockIDGenerationBuilder = stockIDGenerationBuilder;
	}

	public String getBreederIdentifier() {
		return breederIdentifier;
	}

	public void setBreederIdentifier(String breederIdentifier) {
		this.breederIdentifier = breederIdentifier;
	}

	public Integer getNotationNumber() {
		return notationNumber;
	}

	public void setNotationNumber(Integer notationNumber) {
		this.notationNumber = notationNumber;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public Long getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(Long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public InventoryService getInventoryService() {
		return inventoryService;
	}
}
