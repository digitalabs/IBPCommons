package org.generationcp.commons.ruleengine.coding;

import org.generationcp.commons.ruleengine.OrderedRule;
import org.generationcp.commons.ruleengine.RuleException;
import org.generationcp.commons.ruleengine.impl.CodingExpressionResolver;
import org.generationcp.middleware.pojos.workbench.NamingConfiguration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class SuffixRule extends OrderedRule<CodingRuleExecutionContext> {

	public static final String KEY = "Suffix";

	@Resource
	private CodingExpressionResolver codingExpressionResolver;

	@Override
	public Object runRule(CodingRuleExecutionContext context) throws RuleException {

		final List<String> input = context.getCurrentData();
		final NamingConfiguration namingConfiguration = context.getNamingConfiguration();
		String suffix = context.getNamingConfiguration().getSuffix();

		if (suffix == null) {
			suffix = "";
		}

		for (int i = 0; i < input.size(); i++) {
			input.set(i, codingExpressionResolver.resolve(input.get(i), suffix, namingConfiguration).get(0));
		}

		context.setCurrentData(input);

		return input;

	}

	@Override
	public String getKey() {
		return SuffixRule.KEY;
	}
}
