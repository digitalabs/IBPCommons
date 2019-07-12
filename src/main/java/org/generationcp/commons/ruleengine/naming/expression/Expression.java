
package org.generationcp.commons.ruleengine.naming.expression;

import java.util.List;

import org.generationcp.commons.pojo.AdvancingSource;

public interface Expression {

	void apply(List<StringBuilder> values, AdvancingSource source, final String capturedText);

	String getExpressionKey();
}
