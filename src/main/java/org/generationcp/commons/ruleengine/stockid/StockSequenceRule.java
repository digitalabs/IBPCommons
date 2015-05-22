package org.generationcp.commons.ruleengine.stockid;

import org.generationcp.commons.ruleengine.OrderedRule;
import org.generationcp.commons.ruleengine.RuleException;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 * Date: 4/16/2015
 * Time: 3:30 PM
 */
public class StockSequenceRule extends OrderedRule<StockIDGenerationRuleExecutionContext>{
	public static final String KEY = "SEQUENCE";

	@Override public Object runRule(StockIDGenerationRuleExecutionContext context)
			throws RuleException {
		Long currentSequenceNumber = context.getSequenceNumber();

		if (currentSequenceNumber == null) {
			currentSequenceNumber = 0L;
		}

		currentSequenceNumber++;
		context.setSequenceNumber(currentSequenceNumber);
		context.getStockIDGenerationBuilder().append(currentSequenceNumber);

		return currentSequenceNumber;
	}

	@Override public String getKey() {
		return KEY;
	}
}
