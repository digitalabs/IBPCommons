package org.generationcp.commons.ruleengine.naming.expression;

import org.generationcp.commons.ruleengine.ExpressionUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PaddedSequenceExpression extends SequenceExpression {



	@Override
	public Integer getNumberOfDigits(final StringBuilder container) {
		return ExpressionUtils.getNumberOfDigitsFromKey(org.generationcp.commons.ruleengine.coding.expression.PaddedSequenceExpression.PATTERN, container);
	}

	@Override
	public String getExpressionKey() {
		return org.generationcp.commons.ruleengine.coding.expression.PaddedSequenceExpression.PATTERN_KEY;
	}

	@Override
	public Pattern getPattern() {
		return org.generationcp.commons.ruleengine.coding.expression.PaddedSequenceExpression.PATTERN;
	}


}
