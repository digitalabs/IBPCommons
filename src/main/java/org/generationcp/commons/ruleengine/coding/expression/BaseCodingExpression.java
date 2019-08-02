package org.generationcp.commons.ruleengine.coding.expression;

import org.generationcp.commons.ruleengine.Expression;
import org.generationcp.commons.ruleengine.ExpressionUtils;

public abstract class BaseCodingExpression implements Expression {

	void replaceRegularExpressionKeyWithValue(final StringBuilder container, final String value) {
		ExpressionUtils.replaceRegularExpressionKeyWithValue(this.getExpressionKey(), container, value);
	}

}
