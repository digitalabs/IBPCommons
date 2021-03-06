
package org.generationcp.commons.ruleengine.naming.rules;

import java.util.List;

import org.generationcp.commons.ruleengine.OrderedRule;
import org.generationcp.commons.ruleengine.RuleException;
import org.springframework.stereotype.Component;

import org.generationcp.commons.ruleengine.naming.service.ProcessCodeService;
import org.generationcp.commons.pojo.AdvancingSource;

@Component
public class SeparatorRule extends OrderedRule<NamingRuleExecutionContext> {

	public static final String KEY = "Separator";

	@Override
	public Object runRule(NamingRuleExecutionContext context) throws RuleException {
		List<String> input = context.getCurrentData();
		AdvancingSource source = context.getAdvancingSource();

		ProcessCodeService processCodeService = context.getProcessCodeService();
		String separatorExpression = source.getBreedingMethod().getSeparator();

		if (separatorExpression == null) {
			separatorExpression = "";
		}

		for (int i = 0; i < input.size(); i++) {
			// some separator expressions perform operations on the root name, so we replace the current input with the result
			input.set(i, processCodeService.applyProcessCode(input.get(i), separatorExpression, source).get(0));
		}

		context.setCurrentData(input);

		return input;
	}

	@Override
	public String getKey() {
		return SeparatorRule.KEY;
	}
}
