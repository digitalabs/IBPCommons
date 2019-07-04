
package org.generationcp.commons.ruleengine.naming.expression;

import java.util.List;

import org.springframework.stereotype.Component;

import org.generationcp.commons.pojo.AdvancingSource;

@Component
public class NumberExpression extends BaseExpression implements Expression {

	public static final String KEY = "[NUMBER]";

	public NumberExpression() {

	}

	@Override
	public void apply(final List<StringBuilder> values, final AdvancingSource source, final String capturedText) {
		for (final StringBuilder container : values) {
			if (source.getPlantsSelected() != null && source.getPlantsSelected() > 1) {
				final Integer newValue = source.getPlantsSelected();
				this.replaceExpressionWithValue(container, newValue != null ? newValue.toString() : "");
			} else {
				this.replaceExpressionWithValue(container, "");
			}
		}
	}

	@Override
	public String getExpressionKey() {
		return NumberExpression.KEY;
	}

}
