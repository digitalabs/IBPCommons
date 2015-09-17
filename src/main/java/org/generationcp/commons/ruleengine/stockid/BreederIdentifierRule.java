
package org.generationcp.commons.ruleengine.stockid;

import javax.annotation.Resource;

import org.generationcp.commons.ruleengine.OrderedRule;
import org.generationcp.commons.ruleengine.RuleException;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte
 */
@Component
public class BreederIdentifierRule extends OrderedRule<StockIDGenerationRuleExecutionContext> {

	static final String KEY = "IDENTIFIER";

	@Override
	public Object runRule(final StockIDGenerationRuleExecutionContext context) throws RuleException {
		if (context.getBreederIdentifier() == null) {
			throw new IllegalStateException("User must have supplied breeder identifier at this point");
		}

		context.getStockIDGenerationBuilder().append(context.getBreederIdentifier());

		return context.getBreederIdentifier();
	}

	@Override
	public String getKey() {
		return BreederIdentifierRule.KEY;
	}
}
