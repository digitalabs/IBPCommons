package org.generationcp.commons.ruleengine.coding;

import org.generationcp.commons.ruleengine.OrderedRule;
import org.generationcp.commons.ruleengine.RuleException;
import org.generationcp.commons.ruleengine.coding.expression.CodingExpressionResolver;
import org.generationcp.middleware.pojos.workbench.NamingConfiguration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SuffixRule extends OrderedRule<CodingRuleExecutionContext> {

	public static final String KEY = "Suffix";

	@Resource
	private CodingExpressionResolver codingExpressionResolver;

	@Override
	public Object runRule(final CodingRuleExecutionContext context) throws RuleException {

		final NamingConfiguration namingConfiguration = context.getNamingConfiguration();
		String suffix = context.getNamingConfiguration().getSuffix();

		if (suffix == null) {
			suffix = "";
		}

		final String resolvedData = codingExpressionResolver.resolve(context.getCurrentData(), suffix, namingConfiguration).get(0);

		context.setCurrentData(resolvedData);

		return resolvedData;

	}

	@Override
	public String getKey() {
		return SuffixRule.KEY;
	}
}
