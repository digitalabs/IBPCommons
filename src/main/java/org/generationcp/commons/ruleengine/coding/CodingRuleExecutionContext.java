package org.generationcp.commons.ruleengine.coding;

import org.generationcp.commons.ruleengine.OrderedRuleExecutionContext;
import org.generationcp.middleware.pojos.workbench.NamingConfiguration;

import java.util.ArrayList;
import java.util.List;

public class CodingRuleExecutionContext extends OrderedRuleExecutionContext {

	private final NamingConfiguration namingConfiguration;
	private Integer startNumber;
	private List<String> currentData =  new ArrayList<>();

	public CodingRuleExecutionContext(final List<String> executionOrder, final NamingConfiguration namingConfiguration) {
		super(executionOrder);
		this.namingConfiguration = namingConfiguration;
	}

	@Override
	public Object getRuleExecutionOutput() {
		return this.currentData;
	}

	public NamingConfiguration getNamingConfiguration() {
		return namingConfiguration;
	}

	public Integer getStartNumber() {
		return startNumber;
	}

	public void setStartNumber(final Integer startNumber) {
		this.startNumber = startNumber;
	}

	public List<String> getCurrentData() {
		return currentData;
	}

	public void setCurrentData(final List<String> currentData) {
		this.currentData = currentData;
	}

	public void reset() {
		this.getCurrentData().clear();
		this.setCurrentExecutionIndex(0);
	}
}
