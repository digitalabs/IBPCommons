
package org.generationcp.commons.ruleengine.stockid;

import org.generationcp.commons.ruleengine.OrderedRule;
import org.generationcp.commons.ruleengine.RuleException;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 4/16/2015 Time: 3:30 PM
 */
public class StockSequenceRule extends OrderedRule<StockIDGenerationRuleExecutionContext> {

	static final String KEY = "SEQUENCE";

	@Override
	public Object runRule(final StockIDGenerationRuleExecutionContext context) throws RuleException {
		final Long currentSequenceNumber = context.getSequenceNumber() == null ? 0L : context.getSequenceNumber();

		final Long nextSequenceNumber = currentSequenceNumber + 1;


		context.setSequenceNumber(nextSequenceNumber);
		context.getStockIDGenerationBuilder().append(nextSequenceNumber);

		return nextSequenceNumber;
	}

	@Override
	public String getKey() {
		return StockSequenceRule.KEY;
	}
}
