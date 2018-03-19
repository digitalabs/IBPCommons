package org.generationcp.commons.ruleengine;

import org.generationcp.middleware.pojos.workbench.NamingConfiguration;

import java.util.List;

public interface Expression {

	public void apply(List<StringBuilder> values, final String capturedText, final NamingConfiguration namingConfiguration);

	public String getExpressionKey();

}
