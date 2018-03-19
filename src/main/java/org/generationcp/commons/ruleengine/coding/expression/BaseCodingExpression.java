package org.generationcp.commons.ruleengine.coding.expression;

import org.generationcp.commons.ruleengine.Expression;

public abstract class BaseCodingExpression implements Expression {

	protected void replaceExpressionWithValue(StringBuilder container, String value) {
		int startIndex = container.toString().toUpperCase().indexOf(getExpressionKey());
		int endIndex = startIndex + getExpressionKey().length();

		String replaceValue = value == null ? "" : value;
		container.replace(startIndex, endIndex, replaceValue);
	}

}
