package org.generationcp.commons.ruleengine.coding.expression;

import org.generationcp.commons.ruleengine.Expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BaseCodingExpression implements Expression {

	protected void replaceRegularExpressionKeyWithValue(final StringBuilder container, final String value) {
		final Pattern pattern = Pattern.compile(getExpressionKey());
		final Matcher matcher = pattern.matcher(container.toString());
		if (matcher.find()) {
			String replaceValue = value == null ? "" : value;
			container.replace(matcher.start(), matcher.end(), replaceValue);
		}

	}

}
