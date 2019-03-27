
package org.generationcp.commons.ruleengine.naming.expression;

import java.util.List;

import org.springframework.stereotype.Component;

import org.generationcp.commons.pojo.AdvancingSource;

@Component
public class NumberExpression extends NumberSequenceExpression implements Expression {

	public static final String KEY = "[NUMBER]";

	public NumberExpression() {

	}

	@Override
	public void apply(List<StringBuilder> values, AdvancingSource source, final String capturedText) {
		this.applyNumberSequence(values, source);
	}

	@Override
	public String getExpressionKey() {
		return NumberExpression.KEY;
	}
}
