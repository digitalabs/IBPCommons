
package org.generationcp.commons.ruleengine.naming.expression;

import org.generationcp.commons.pojo.AdvancingSource;

import java.util.List;

public abstract class NumberSequenceExpression extends BaseExpression {

	void applyNumberSequenceForBulking(final List<StringBuilder> values, final AdvancingSource source) {
		if (source.isForceUniqueNameGeneration()) {
			for (final StringBuilder container : values) {
				this.replaceExpressionWithValue(container, "(" + (source.getCurrentMaxSequence() + 1) + ")");

			}

			return;
		}

		for (final StringBuilder container : values) {
			if (source.getPlantsSelected() != null && source.getPlantsSelected() > 1) {
				final Integer newValue = source.getPlantsSelected();
				this.replaceExpressionWithValue(container, newValue != null ? newValue.toString() : "");
			} else {
				this.replaceExpressionWithValue(container, "");
			}
		}
	}
}
