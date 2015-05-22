package org.generationcp.commons.ruleengine.stockid;

import org.generationcp.commons.ruleengine.OrderedRule;
import org.generationcp.commons.ruleengine.RuleException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 */
@Component
public class BreederIdentifierRule extends OrderedRule<StockIDGenerationRuleExecutionContext>{

	public static final String KEY = "IDENTIFIER";

	@Resource
	private BreederIdentifierGenerationStrategy generationStrategy;

	@Override public Object runRule(StockIDGenerationRuleExecutionContext context)
			throws RuleException {
		if (context.getBreederIdentifier() == null) {
			context.setBreederIdentifier(generationStrategy.generateBreederIdentifier());
		}

		context.getStockIDGenerationBuilder().append(context.getBreederIdentifier());

		return context.getBreederIdentifier();
	}

	@Override public String getKey() {
		return KEY;
	}
}
