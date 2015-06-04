
package org.generationcp.commons.ruleengine.stockid;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.ruleengine.OrderedRule;
import org.generationcp.commons.ruleengine.RuleException;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 4/16/2015 Time: 3:42 PM
 */
@Component
public class StockIDSeparatorRule extends OrderedRule<StockIDGenerationRuleExecutionContext> {

	public static final String KEY = "SEPARATOR";
	public static final String DEFAULT_SEPARATOR = "-";

	@Override
	public Object runRule(StockIDGenerationRuleExecutionContext context) throws RuleException {
		String separator = context.getSeparator();
		if (StringUtils.isEmpty(context.getSeparator())) {
			separator = StockIDSeparatorRule.DEFAULT_SEPARATOR;
			context.setSeparator(StockIDSeparatorRule.DEFAULT_SEPARATOR);
		}

		context.getStockIDGenerationBuilder().append(separator);

		return separator;
	}

	@Override
	public String getKey() {
		return StockIDSeparatorRule.KEY;
	}
}
