
package org.generationcp.commons.ruleengine.stockid;

import java.util.List;

import org.generationcp.commons.ruleengine.OrderedRuleExecutionContext;
import org.generationcp.middleware.service.api.InventoryService;

/**
 * An object that serves to provide a common context between rules that interact together for the purpose of stock ID generation. This
 * allows the preservation / storage of state between rule executions.
 */
public class StockIDGenerationRuleExecutionContext extends OrderedRuleExecutionContext {

	private InventoryService inventoryService;
	private StringBuilder stockIDGenerationBuilder;
	private String breederIdentifier;
	private Integer notationNumber;
	private String separator;
	private Long sequenceNumber;

	public StockIDGenerationRuleExecutionContext(final List<String> executionOrder) {
		this(executionOrder, null);
	}

	public StockIDGenerationRuleExecutionContext(final List<String> executionOrder, final InventoryService inventoryService) {
		super(executionOrder);
		this.inventoryService = inventoryService;
		this.stockIDGenerationBuilder = new StringBuilder();
	}

	@Override
	public Object getRuleExecutionOutput() {
		return this.stockIDGenerationBuilder.toString();
	}

	public StringBuilder getStockIDGenerationBuilder() {
		return this.stockIDGenerationBuilder;
	}

	public void setStockIDGenerationBuilder(final StringBuilder stockIDGenerationBuilder) {
		this.stockIDGenerationBuilder = stockIDGenerationBuilder;
	}

	public String getBreederIdentifier() {
		return this.breederIdentifier;
	}

	public void setBreederIdentifier(final String breederIdentifier) {
		this.breederIdentifier = breederIdentifier;
	}

	public Integer getNotationNumber() {
		return this.notationNumber;
	}

	public void setNotationNumber(Integer notationNumber) {
		this.notationNumber = notationNumber;
	}

	public String getSeparator() {
		return this.separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public Long getSequenceNumber() {
		return this.sequenceNumber;
	}

	public void setSequenceNumber(Long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public InventoryService getInventoryService() {
		return this.inventoryService;
	}
}
