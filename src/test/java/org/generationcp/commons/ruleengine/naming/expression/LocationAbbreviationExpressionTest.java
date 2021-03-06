
package org.generationcp.commons.ruleengine.naming.expression;

import java.util.List;

import org.junit.Test;

import org.generationcp.commons.pojo.AdvancingSource;

public class LocationAbbreviationExpressionTest extends TestExpression {

	@Test
	public void testLabbrAsPrefix() throws Exception {
		LocationAbbreviationExpression expression = new LocationAbbreviationExpression();
		AdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", null, "[LABBR]", null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testLabbrAsSuffix() throws Exception {
		LocationAbbreviationExpression expression = new LocationAbbreviationExpression();
		AdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", ":", null, null, "[LABBR]", true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testNoLabbr() throws Exception {
		LocationAbbreviationExpression expression = new LocationAbbreviationExpression();
		AdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", null, null, null, "[LABBR]", true);
		source.setLocationAbbreviation(null);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testCaseSensitive() throws Exception {
		LocationAbbreviationExpression expression = new LocationAbbreviationExpression();
		AdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", null, "[labbr]", null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		System.out.println("process code is in lower case");
		this.printResult(values, source);
	}

}
