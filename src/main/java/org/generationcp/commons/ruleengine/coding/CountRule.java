package org.generationcp.commons.ruleengine.coding;

import org.generationcp.commons.ruleengine.OrderedRule;
import org.generationcp.commons.ruleengine.RuleException;
import org.generationcp.commons.ruleengine.impl.CodingExpressionResolver;
import org.generationcp.middleware.pojos.workbench.NamingConfiguration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class CountRule extends OrderedRule<CodingRuleExecutionContext> {

	public static final String KEY = "Count";

	@Resource
	private CodingExpressionResolver codingExpressionResolver;

	@Override
	public Object runRule(final CodingRuleExecutionContext context) throws RuleException {

		final NamingConfiguration namingConfiguration = context.getNamingConfiguration();
		String count = context.getNamingConfiguration().getCount();

		if (count == null) {
			count = "";
		}

		final String resolvedData = codingExpressionResolver.resolve(context.getCurrentData(), count, namingConfiguration).get(0);

		context.setCurrentData(resolvedData);

		return resolvedData;

	}

	@Override
	public String getKey() {
		return CountRule.KEY;
	}
}
