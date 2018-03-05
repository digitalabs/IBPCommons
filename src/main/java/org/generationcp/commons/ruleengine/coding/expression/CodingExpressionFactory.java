package org.generationcp.commons.ruleengine.coding.expression;

import java.util.HashMap;
import java.util.Map;

public class CodingExpressionFactory {

	private Map<String, Expression> expressionMap;

	public void init() {

		this.expressionMap = new HashMap<>();

	}

	public Expression create(final String key) {
		return this.expressionMap.get(key.toUpperCase());
	}

	/**
	 * @param pattern
	 * @return the first Expression that match the pattern
	 */
	public Expression lookup(final String pattern) {
		if (expressionMap.containsKey(pattern)) {
			return expressionMap.get(pattern);
		}
		for (final String key : expressionMap.keySet()) {
			if (key != null && pattern.matches(key)) {
				final Expression expression = expressionMap.get(key);
				expressionMap.put(pattern, expression); // memoize
				return expression;
			}
		}
		return null;
	}

	public void addExpression(final Expression expression) {
		expressionMap.put(expression.getExpressionKey(), expression);
	}
}
