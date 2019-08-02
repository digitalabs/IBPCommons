package org.generationcp.commons.ruleengine.naming.expression;

import org.generationcp.commons.ruleengine.ExpressionUtils;
import org.springframework.stereotype.Component;

@Component
public class PaddedSequenceExpression extends SequenceExpression {

	@Override
	public Integer getNumberOfDigits(final StringBuilder container) {
		return ExpressionUtils.getNumberOfDigitsFromKey(this.getExpressionKey(), container);
	}

	@Override
	public String getExpressionKey() {
		return org.generationcp.commons.ruleengine.coding.expression.PaddedSequenceExpression.PATTERN_KEY;
	}

}
