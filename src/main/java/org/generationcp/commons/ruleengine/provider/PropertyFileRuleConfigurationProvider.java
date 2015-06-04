
package org.generationcp.commons.ruleengine.provider;

import java.util.Map;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 2/14/2015 Time: 6:55 AM
 */
public class PropertyFileRuleConfigurationProvider implements RuleConfigurationProvider {

	private Map<String, String[]> ruleSequenceConfiguration;

	@Override
	public Map<String, String[]> retrieveRuleSequenceConfiguration() {
		return this.ruleSequenceConfiguration;
	}

	public void setRuleSequenceConfiguration(Map<String, String[]> ruleSequenceConfiguration) {
		this.ruleSequenceConfiguration = ruleSequenceConfiguration;
	}
}
