package org.generationcp.commons.ruleengine.impl;

import org.generationcp.commons.ruleengine.Rule;
import org.generationcp.commons.ruleengine.RuleException;
import org.generationcp.commons.ruleengine.RuleExecutionContext;
import org.generationcp.commons.ruleengine.RuleFactory;
import org.generationcp.commons.ruleengine.service.RulesService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;


public class RulesServiceImpl implements RulesService{

	@Resource
	private RuleFactory ruleFactory;
	
	public RulesServiceImpl(){}
	
	// FIXME : catch RuleExceptions here?
	public Object runRules(RuleExecutionContext context) throws RuleException {
		List<String> sequenceOrder = context.getExecutionOrder();

		assert (!sequenceOrder.isEmpty());
		Rule rule = ruleFactory.getRule(sequenceOrder.get(0));

		while (rule != null) {
			rule.runRule(context);
			rule = ruleFactory.getRule(rule.getNextRuleStepKey(context));
		}

		return context.getRuleExecutionOutput();

	}

	public void setRuleFactory(RuleFactory ruleFactory) {
		this.ruleFactory = ruleFactory;
	}
}