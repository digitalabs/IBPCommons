
package org.generationcp.commons.ruleengine.naming.expression;

import java.util.List;

import org.junit.Test;

import org.generationcp.commons.pojo.AdvancingSource;

public class TopLocationAbbreviationExpressionTest extends TestExpression {

	@Test
	public void testLabbrAsPrefix() throws Exception {
		TopLocationAbbreviationExpression expression = new TopLocationAbbreviationExpression();
		AdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", null, "[TLABBR]", null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testLabbrAsSuffix() throws Exception {
		TopLocationAbbreviationExpression expression = new TopLocationAbbreviationExpression();
		AdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", ":", null, null, "[TLABBR]", true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testNoLabbr() throws Exception {
		TopLocationAbbreviationExpression expression = new TopLocationAbbreviationExpression();
		AdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", null, null, null, "[TLABBR]", true);
		source.setLocationAbbreviation(null);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		this.printResult(values, source);
	}

	@Test
	public void testCaseSensitive() throws Exception {
		TopLocationAbbreviationExpression expression = new TopLocationAbbreviationExpression();
		AdvancingSource source = this.createAdvancingSourceTestData("GERMPLASM_TEST", null, "[tLabbr]", null, null, true);
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, null);
		System.out.println("process code is in lower case");
		this.printResult(values, source);
	}

}
