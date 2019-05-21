
package org.generationcp.commons.ruleengine.naming.rules;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.commons.ruleengine.OrderedRule;
import org.generationcp.commons.ruleengine.RuleException;
import org.springframework.stereotype.Component;

import org.generationcp.commons.ruleengine.naming.expression.RootNameExpression;
import org.generationcp.commons.pojo.AdvancingSource;

@Component
public class RootNameGeneratorRule extends OrderedRule<NamingRuleExecutionContext> {

	public static final String KEY = "RootNameGenerator";

	@Override
	public Object runRule(NamingRuleExecutionContext context) throws RuleException {

		RootNameExpression rootNameExpression = new RootNameExpression();
		AdvancingSource advancingSource = context.getAdvancingSource();

		List<StringBuilder> builders = new ArrayList<>();
		builders.add(new StringBuilder());
		rootNameExpression.apply(builders, advancingSource, null);

		List<String> input = context.getCurrentData();

		String name = builders.get(0).toString();

		input.add(name);

		context.setCurrentData(input);

		return input;
	}

	@Override
	public String getKey() {
		return RootNameGeneratorRule.KEY;
	}
}
