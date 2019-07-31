package org.generationcp.commons.ruleengine.coding.expression;

import org.generationcp.commons.ruleengine.Expression;
import org.generationcp.commons.ruleengine.ExpressionUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BaseCodingExpression implements Expression {

	protected void replaceRegularExpressionKeyWithValue(final StringBuilder container, final String value) {
		ExpressionUtils.replaceRegularExpressionKeyWithValue(this.getExpressionKey(), container, value);
	}

}
