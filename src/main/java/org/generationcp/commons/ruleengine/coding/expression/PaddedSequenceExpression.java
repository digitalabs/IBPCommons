package org.generationcp.commons.ruleengine.coding.expression;

import org.generationcp.commons.ruleengine.ExpressionUtils;

public class PaddedSequenceExpression extends SequenceExpression {

	private static final String PADSEQ_BASE = "PADSEQ";
	public static final String PATTERN_KEY = "\\[" + PADSEQ_BASE + "(\\.[0-9]+)*\\]";

	@Override
	public Integer getNumberOfDigits(final StringBuilder container) {
		return ExpressionUtils.getNumberOfDigitsFromKey(PATTERN_KEY, container);
	}

	@Override
	public String getExpressionKey() {
		return PaddedSequenceExpression.PATTERN_KEY;
	}

}
