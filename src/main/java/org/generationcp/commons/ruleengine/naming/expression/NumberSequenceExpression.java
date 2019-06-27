
package org.generationcp.commons.ruleengine.naming.expression;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.commons.pojo.AdvancingSource;
import org.generationcp.commons.ruleengine.naming.expression.BaseExpression;
import org.generationcp.commons.service.GermplasmNamingService;
import org.generationcp.middleware.service.api.KeySequenceRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public abstract class NumberSequenceExpression extends BaseExpression {

	protected void applyNumberSequenceForBulking(List<StringBuilder> values, AdvancingSource source) {
		if (source.isForceUniqueNameGeneration()) {
			for (StringBuilder container : values) {
				this.replaceExpressionWithValue(container, "(" + Integer.toString(source.getCurrentMaxSequence() + 1) + ")");

			}

			return;
		}

		for (StringBuilder container : values) {
			if (source.getPlantsSelected() != null && source.getPlantsSelected() > 1) {
				Integer newValue = source.getPlantsSelected();
				this.replaceExpressionWithValue(container, newValue != null ? newValue.toString() : "");
			} else {
				this.replaceExpressionWithValue(container, "");
			}
		}
	}
}
