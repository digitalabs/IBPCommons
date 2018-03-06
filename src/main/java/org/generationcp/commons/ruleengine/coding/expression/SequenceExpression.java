package org.generationcp.commons.ruleengine.coding.expression;

import org.generationcp.middleware.pojos.workbench.NamingConfiguration;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SequenceExpression extends BaseCodingExpression {

	public static final String KEY = "[SEQUENCE]";

	@Override
	public void apply(List<StringBuilder> values, final String capturedText, final NamingConfiguration namingConfiguration) {

		int sequence = namingConfiguration.getSequenceCounter();

		for (StringBuilder container : values) {
			replaceExpressionWithValue(container, String.valueOf(namingConfiguration.getSequenceCounter()));
		}

		namingConfiguration.setSequenceCounter(++sequence);

	}

	@Override
	public String getExpressionKey() {
		return SequenceExpression.KEY;
	}

}
