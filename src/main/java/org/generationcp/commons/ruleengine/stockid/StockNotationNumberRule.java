
package org.generationcp.commons.ruleengine.stockid;

import org.generationcp.commons.ruleengine.OrderedRule;
import org.generationcp.commons.ruleengine.RuleException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 4/9/2015 Time: 5:13 PM
 */
@Component
public class StockNotationNumberRule extends OrderedRule<StockIDGenerationRuleExecutionContext> {

	static final String KEY = "NOTATION";

	@Override
	public Object runRule(final StockIDGenerationRuleExecutionContext context) throws RuleException {

		try {
			final Integer notationNumber =
					context.getInventoryService().getCurrentNotationNumberForBreederIdentifier(context.getBreederIdentifier()) + 1;
			context.setNotationNumber(notationNumber);
		} catch (MiddlewareQueryException e) {
			throw new RuleException(e.getMessage(), e);
		}

		final Integer currentNotationNumber = context.getNotationNumber();
		context.getStockIDGenerationBuilder().append(currentNotationNumber);

		return currentNotationNumber;
	}

	@Override
	public String getKey() {
		return StockNotationNumberRule.KEY;
	}
}
