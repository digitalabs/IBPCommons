package org.generationcp.commons.ruleengine.coding;

import org.generationcp.commons.ruleengine.OrderedRule;
import org.generationcp.commons.ruleengine.RuleException;
import org.generationcp.commons.ruleengine.impl.CodingExpressionResolver;
import org.generationcp.middleware.pojos.workbench.NamingConfiguration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class PrefixRule extends OrderedRule<CodingRuleExecutionContext> {

	public static final String KEY = "Prefix";

	@Resource
	private CodingExpressionResolver codingExpressionResolver;

	@Override
	public Object runRule(final CodingRuleExecutionContext context) throws RuleException {

		final NamingConfiguration namingConfiguration = context.getNamingConfiguration();
		String prefix = context.getNamingConfiguration().getPrefix();

		if (prefix == null) {
			prefix = "";
		}

		final String resolvedData = codingExpressionResolver.resolve(context.getCurrentData(), prefix, namingConfiguration).get(0);

		context.setCurrentData(resolvedData);

		return resolvedData;

	}

	@Override
	public String getKey() {
		return PrefixRule.KEY;
	}
}
