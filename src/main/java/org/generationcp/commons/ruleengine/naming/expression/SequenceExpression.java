
package org.generationcp.commons.ruleengine.naming.expression;

import java.util.List;

import org.springframework.stereotype.Component;

import org.generationcp.commons.pojo.AdvancingSource;

@Component
public class SequenceExpression extends NumberSequenceExpression implements Expression {

	public static final String KEY = "[SEQUENCE]";

	public SequenceExpression() {
	}

	@Override
	public void apply(List<StringBuilder> values, AdvancingSource source, final String capturedText) {
		this.applyNumberSequence(values, source);
	}

	@Override
	public String getExpressionKey() {
		return SequenceExpression.KEY;
	}
}
