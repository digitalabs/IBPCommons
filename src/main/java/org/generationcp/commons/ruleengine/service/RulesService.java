
package org.generationcp.commons.ruleengine.service;

import org.generationcp.commons.ruleengine.RuleException;
import org.generationcp.commons.ruleengine.RuleExecutionContext;

public interface RulesService {

	public Object runRules(RuleExecutionContext context) throws RuleException;
}
