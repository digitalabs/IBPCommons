package org.generationcp.commons.ruleengine.naming.expression;

import org.generationcp.commons.parsing.pojo.ImportedGermplasm;
import org.generationcp.commons.pojo.AdvancingSource;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Method;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


@RunWith(MockitoJUnitRunner.class)
public class AttributeSourceExpressionTest extends TestExpression {

	@Mock
	private GermplasmDataManager germplasmDataManager;

	@InjectMocks
	AttributeSourceExpression expression = new AttributeSourceExpression();

	private static final Integer VARIABLE_ID = 2000;

	private static final String PREFIX = "[ATTRSC.2000]";

	private static final String COUNT = "[SEQUENCE]";


	@Test
	public void testAttributeAsPrefix() throws Exception {
		Mockito.when(germplasmDataManager.getAttributeValue(1000, VARIABLE_ID)).thenReturn("AA");
		final Method derivativeMethod = this.createDerivativeMethod(PREFIX, COUNT, null, "-", true);
		final ImportedGermplasm importedGermplasm =
			this.createImportedGermplasm(1, "(AA/ABC)", "1000", 104, 104, -1, derivativeMethod.getMid());
		final AdvancingSource source =
			this.createAdvancingSourceTestData(derivativeMethod, importedGermplasm, "(AA/ABC)", "Dry", "NurseryTest");
		final List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, PREFIX);
		this.printResult(values, source);
		assertThat(values.get(0).toString(), is(equalTo("(AA/ABC)-AA[SEQUENCE]")));
	}

	@Test
	public void testAttributeAsPrefixMultipleCopies() throws Exception {
		Mockito.when(germplasmDataManager.getAttributeValue(1000, VARIABLE_ID)).thenReturn("AA");
		final Method derivativeMethod = this.createDerivativeMethod(PREFIX + PREFIX, COUNT, null, "-", true);
		final ImportedGermplasm importedGermplasm =
			this.createImportedGermplasm(1, "(AA/ABC)", "1000", 104, 104, -1, derivativeMethod.getMid());
		final AdvancingSource source =
			this.createAdvancingSourceTestData(derivativeMethod, importedGermplasm, "(AA/ABC)", "Dry", "NurseryTest");
		final List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, PREFIX);
		this.printResult(values, source);
		assertThat(values.get(0).toString(), is(equalTo("(AA/ABC)-AAAA[SEQUENCE]")));
	}

	@Test
	public void testAttributeAsPrefixWithOutAttributeValue() throws Exception {
		Mockito.when(germplasmDataManager.getAttributeValue(1000, VARIABLE_ID)).thenReturn("");
		final Method derivativeMethod = this.createDerivativeMethod(PREFIX, COUNT, null, "-", true);
		final ImportedGermplasm importedGermplasm =
			this.createImportedGermplasm(1, "(AA/ABC)", "1000", 104, 104, -1, derivativeMethod.getMid());
		final AdvancingSource source =
			this.createAdvancingSourceTestData(derivativeMethod, importedGermplasm, "(AA/ABC)", "Dry", "NurseryTest");
		final List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, PREFIX);
		this.printResult(values, source);
		assertThat(values.get(0).toString(), is(equalTo("(AA/ABC)-[SEQUENCE]")));
	}

	@Test
	public void testAttributeAsPrefixGpid2Unknown() throws Exception {

		Mockito.when(germplasmDataManager.getAttributeValue(1000, VARIABLE_ID)).thenReturn("AA");
		final Method derivativeMethod = this.createDerivativeMethod(PREFIX, COUNT, null, "-", true);
		final ImportedGermplasm importedGermplasm =
			this.createImportedGermplasm(1, "(AA/ABC)", "1000", 0, 0, -1, derivativeMethod.getMid());
		AdvancingSource source = this.createAdvancingSourceTestData(derivativeMethod, importedGermplasm, "(AA/ABC)", "Dry", "NurseryTest");
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, PREFIX);
		this.printResult(values, source);
		assertThat(values.get(0).toString(), is(equalTo("(AA/ABC)-AA[SEQUENCE]")));
	}

	@Test
	public void testAttributeAsPrefixInvalidBreedingMethod() throws Exception {

		final Method generativeMethod = this.createGenerativeMethod(PREFIX, COUNT, null, "-", true);
		final ImportedGermplasm importedGermplasm =
			this.createImportedGermplasm(1, "(AA/ABC)", "1000", 104, 104, -1, generativeMethod.getMid());
		AdvancingSource source =
			this.createAdvancingSourceTestData(generativeMethod, importedGermplasm, "(AA/ABC)", "Dry", "NurseryTest");
		List<StringBuilder> values = this.createInitialValues(source);
		expression.apply(values, source, PREFIX);
		this.printResult(values, source);
		assertThat(values.get(0).toString(),is(equalTo("(AA/ABC)-[SEQUENCE]")));
	}
}
