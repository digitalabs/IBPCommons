
package org.generationcp.commons.ruleengine;

import java.util.List;

/**
 * Base interface for context objects used to store state for rule executions
 */
public interface RuleExecutionContext {

	public List<String> getExecutionOrder();

	public Object getRuleExecutionOutput();

}
