
package org.generationcp.commons.ruleengine;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;

import org.generationcp.commons.ruleengine.provider.RuleConfigurationProvider;

public class RuleFactory {

	private Map<String, Rule> availableRules;

	private Map<String, String[]> ruleOrder;

	@Resource
	private RuleConfigurationProvider configProvider;

	public RuleFactory() {
		this.availableRules = new HashMap<>();
		this.ruleOrder = new HashMap<>();
	}

	public void init() {
		this.ruleOrder = this.configProvider.retrieveRuleSequenceConfiguration();
	}

	public void setAvailableRules(Map<String, Rule> availableRulesMap) {
		this.availableRules = availableRulesMap;
	}

	public void addRule(Rule rule) {
		this.availableRules.put(rule.getKey(), rule);
	}

	public Rule getRule(String key) {
		if (key == null) {
			return null;
		}

		return this.availableRules.get(key);
	}

	public int getAvailableRuleCount() {
		return this.availableRules.size();
	}

	public String[] getRuleSequenceForNamespace(String namespace) {
		if (!this.ruleOrder.containsKey(namespace)) {
			return null;
		}

		return this.ruleOrder.get(namespace);
	}

	public Collection<String> getAvailableConfiguredNamespaces() {
		return this.ruleOrder.keySet();
	}
}
