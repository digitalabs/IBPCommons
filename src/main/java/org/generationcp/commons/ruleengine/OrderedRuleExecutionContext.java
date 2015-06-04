
package org.generationcp.commons.ruleengine;

import java.util.List;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 2/18/2015 Time: 12:07 PM
 */
public abstract class OrderedRuleExecutionContext implements RuleExecutionContext {

	private final List<String> executionOrder;
	private int executionIndex;

	public OrderedRuleExecutionContext(List<String> executionOrder) {
		this.executionOrder = executionOrder;
	}

	@Override
	public int getCurrentExecutionIndex() {
		return this.executionIndex;
	}

	@Override
	public List<String> getExecutionOrder() {
		return this.executionOrder;
	}

	@Override
	public void setCurrentExecutionIndex(int index) {
		this.executionIndex = index;
	}
}
