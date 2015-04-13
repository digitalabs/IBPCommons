package org.generationcp.commons.ruleengine;

import org.generationcp.commons.ruleengine.OrderedRuleExecutionContext;

import java.util.List;

import org.generationcp.commons.ruleengine.Rule;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 * Date: 2/13/2015
 * Time: 4:07 PM
 */
public abstract class OrderedRule<T extends OrderedRuleExecutionContext> implements Rule<T>{

	@Override public String getNextRuleStepKey(T context){
		List<String> sequenceOrder = context.getExecutionOrder();
		int executionIndex = context.getCurrentExecutionIndex();

		// increment to the next rule in the sequence
		executionIndex++;
		if (executionIndex < sequenceOrder.size()) {
			String nextKey = sequenceOrder.get(executionIndex);
			context.setCurrentExecutionIndex(executionIndex);
			return nextKey;
		} else {
			return null;
		}
	}
}