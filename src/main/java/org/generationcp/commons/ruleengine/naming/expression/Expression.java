
package org.generationcp.commons.ruleengine.naming.expression;

import java.util.List;

import org.generationcp.commons.pojo.AdvancingSource;

public interface Expression {

	public void apply(List<StringBuilder> values, AdvancingSource source, final String capturedText);

	public String getExpressionKey();
}
