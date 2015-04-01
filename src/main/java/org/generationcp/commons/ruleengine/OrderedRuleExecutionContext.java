package org.generationcp.commons.ruleengine;


import java.util.List;

import org.generationcp.commons.ruleengine.RuleExecutionContext;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 * Date: 2/18/2015
 * Time: 12:07 PM
 */
public abstract class OrderedRuleExecutionContext implements RuleExecutionContext{
	private List<String> executionOrder;
	private int executionIndex;

	public OrderedRuleExecutionContext(List<String> executionOrder) {
		this.executionOrder = executionOrder;
	}

	@Override public int getCurrentExecutionIndex() {
		return executionIndex;
	}

	@Override public List<String> getExecutionOrder() {
		return executionOrder;
	}

	@Override public void setCurrentExecutionIndex(int index) {
		this.executionIndex = index;
	}
}
