
package org.generationcp.commons.ruleengine.impl;

import java.util.List;

import javax.annotation.Resource;

import org.generationcp.commons.ruleengine.Rule;
import org.generationcp.commons.ruleengine.RuleException;
import org.generationcp.commons.ruleengine.RuleExecutionContext;
import org.generationcp.commons.ruleengine.RuleFactory;
import org.generationcp.commons.ruleengine.service.RulesService;

public class RulesServiceImpl implements RulesService {

	@Resource
	private RuleFactory ruleFactory;

	public RulesServiceImpl() {
	}

	// FIXME : catch RuleExceptions here?
	@Override
	public Object runRules(RuleExecutionContext context) throws RuleException {
		List<String> sequenceOrder = context.getExecutionOrder();

		assert !sequenceOrder.isEmpty();
		Rule rule = this.ruleFactory.getRule(sequenceOrder.get(0));

		while (rule != null) {
			rule.runRule(context);
			rule = this.ruleFactory.getRule(rule.getNextRuleStepKey(context));
		}

		return context.getRuleExecutionOutput();

	}

	public void setRuleFactory(RuleFactory ruleFactory) {
		this.ruleFactory = ruleFactory;
	}
}
