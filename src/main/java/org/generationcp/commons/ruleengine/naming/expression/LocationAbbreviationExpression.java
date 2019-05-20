
package org.generationcp.commons.ruleengine.naming.expression;

import java.util.List;

import org.springframework.stereotype.Component;

import org.generationcp.commons.pojo.AdvancingSource;

@Component
public class LocationAbbreviationExpression extends BaseExpression {

	public static final String KEY = "[LABBR]";

	public LocationAbbreviationExpression() {
	}

	@Override
	public void apply(List<StringBuilder> values, AdvancingSource source, final String capturedText) {
		for (StringBuilder container : values) {
			String newValue = source.getLocationAbbreviation();
			this.replaceExpressionWithValue(container, newValue);
		}
	}

	@Override
	public String getExpressionKey() {
		return LocationAbbreviationExpression.KEY;
	}
}
