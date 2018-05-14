
package org.generationcp.commons.derivedvariable;

import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DerivedVariableProcessorTest {

	private static final String TERM_1 = "GrainWghtg";
	private static final String TERM_2 = " Moisture";
	private static final String TERM_3 = "\"PlotSize\"";
	private static final String UNMODIFIED_TERM_2 = "% Moisture";
	private static final String TERM_VALUE_1 = "1000";
	private static final String TERM_VALUE_2 = "12.5";
	private static final String TERM_VALUE_3 = "10";
	private static final String TERM_NOT_FOUND = "TermNotFound";
	private static final String FORMULA_1 = "(\"{" + DerivedVariableProcessorTest.TERM_1 + "}\"/100)*((100-{"
			+ DerivedVariableProcessorTest.TERM_2 + "})/(100-12.5))*(10/{" + DerivedVariableProcessorTest.TERM_3 + "})";
	private static final String EXPECTED_FORMULA_1_RESULT = "10";
	private static final String FORMULA_2 = "{PlotSize}*6.23";
	private static final String EXPECTED_FORMULA_2_RESULT = "62.3";
	private DerivedVariableProcessor derivedVariableProcessor;
	private Map<String, Object> terms;
	private String formula;

	@Before
	public void setUp() {
		this.derivedVariableProcessor = new DerivedVariableProcessor();
	}

	@Test
	public void testRemoveAllInvalidCharacters() {
		String term = this.derivedVariableProcessor.removeAllInvalidCharacters(DerivedVariableProcessorTest.UNMODIFIED_TERM_2);
		Assert.assertEquals(DerivedVariableProcessorTest.UNMODIFIED_TERM_2 + " should become " + DerivedVariableProcessorTest.TERM_2,
				DerivedVariableProcessorTest.TERM_2, term);

		String nullTerm = this.derivedVariableProcessor.removeAllInvalidCharacters(null);
		Assert.assertEquals(null + " should become " + null, null, nullTerm);
	}

	@Test
	public void testExtractTermsFromFormula() {
		if (this.terms == null) {
			this.terms = this.derivedVariableProcessor.extractTermsFromFormula(DerivedVariableProcessorTest.FORMULA_1);
		}
		Assert.assertNotNull("Terms should be extracted from formula", this.terms);
		Assert.assertTrue(DerivedVariableProcessorTest.TERM_1 + " should be one of the extracted terms",
				this.terms.containsKey(DerivedVariableProcessorTest.TERM_1));
		Assert.assertTrue(DerivedVariableProcessorTest.TERM_2 + " should be one of the extracted terms",
				this.terms.containsKey(DerivedVariableProcessorTest.TERM_2.trim()));
		Assert.assertFalse(DerivedVariableProcessorTest.TERM_NOT_FOUND + " should not be one of the extracted terms",
				this.terms.containsKey(DerivedVariableProcessorTest.TERM_NOT_FOUND));
	}

	@Test
	public void testFetchTermValuesFromMeasurement() {
		if (this.terms == null) {
			this.terms = this.derivedVariableProcessor.extractTermsFromFormula(DerivedVariableProcessorTest.FORMULA_1);
		}
		this.derivedVariableProcessor.fetchTermValuesFromMeasurement(this.terms, this.createMeasurementRowTestData());
		Assert.assertNotNull("Terms should not be null", this.terms);
		for (Map.Entry<String, Object> entry : this.terms.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			Assert.assertNotNull(key + " should have a value", value);
		}
	}

	@Test
	public void testFetchTermValuesFromMeasurement_NullMeasurementRow() {
		Map<String, Object> testTerms = this.derivedVariableProcessor.extractTermsFromFormula(DerivedVariableProcessorTest.FORMULA_1);
		this.derivedVariableProcessor.fetchTermValuesFromMeasurement(testTerms, null);
		for (Map.Entry<String, Object> entry : testTerms.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			Assert.assertNull(key + " should not have a value", value);
		}
	}

	@Test
	public void testFetchTermValuesFromMeasurement_NullMeasurementDataList() {
		Map<String, Object> testTerms = this.derivedVariableProcessor.extractTermsFromFormula(DerivedVariableProcessorTest.FORMULA_1);
		MeasurementRow measurementRow = new MeasurementRow();
		measurementRow.setDataList(null);
		this.derivedVariableProcessor.fetchTermValuesFromMeasurement(testTerms, measurementRow);
		for (Map.Entry<String, Object> entry : testTerms.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			Assert.assertNull(key + " should not have a value", value);
		}
	}

	private MeasurementRow createMeasurementRowTestData() {
		MeasurementRow measurementRow = new MeasurementRow();
		measurementRow.setDataList(this.createMeasurementDataListTestData());
		return measurementRow;
	}

	private List<MeasurementData> createMeasurementDataListTestData() {
		List<MeasurementData> measurementDataList = new ArrayList<>();
		measurementDataList.add(this.createMeasurementDataTestData(DerivedVariableProcessorTest.TERM_1,
				DerivedVariableProcessorTest.TERM_VALUE_1, null));
		measurementDataList.add(this.createMeasurementDataTestData(DerivedVariableProcessorTest.UNMODIFIED_TERM_2, null,
				DerivedVariableProcessorTest.TERM_VALUE_2));
		measurementDataList.add(this.createMeasurementDataTestData(DerivedVariableProcessorTest.TERM_3,
				DerivedVariableProcessorTest.TERM_VALUE_3, null));
		return measurementDataList;
	}

	private MeasurementData createMeasurementDataTestData(String label, String value, String cValueId) {
		MeasurementData measurementData = new MeasurementData();
		measurementData.setLabel(label);
		measurementData.setValue(value);
		measurementData.setcValueId(cValueId);
		return measurementData;
	}

	@Test
	public void testRemoveCurlyBracesFromFormula() {
		this.formula = this.derivedVariableProcessor.replaceBraces(DerivedVariableProcessorTest.FORMULA_1);
		Assert.assertFalse(this.formula.contains("{"));
		Assert.assertFalse(this.formula.contains("}"));
	}

	@Test
	public void testRemoveCurlyBracesFromFormula_NullFormula() {
		String nullFormula = this.derivedVariableProcessor.replaceBraces(null);
		Assert.assertNull(nullFormula);
	}

	@Test
	public void testEvaluateFormula() {
		this.formula = FORMULA_1;
		this.terms = this.derivedVariableProcessor.extractTermsFromFormula(DerivedVariableProcessorTest.FORMULA_1);
		this.derivedVariableProcessor.fetchTermValuesFromMeasurement(this.terms, this.createMeasurementRowTestData());
		String result = this.derivedVariableProcessor.evaluateFormula(this.formula, this.terms);
		Assert.assertEquals("The result of " + this.formula + " should be " + DerivedVariableProcessorTest.EXPECTED_FORMULA_1_RESULT
				+ " but got " + result, DerivedVariableProcessorTest.EXPECTED_FORMULA_1_RESULT, result);
	}

	@Test
	public void testGetDerivedVariableValue() {
		String result =
				this.derivedVariableProcessor.getDerivedVariableValue(DerivedVariableProcessorTest.FORMULA_2,
						this.createMeasurementRowTestData());
		Assert.assertEquals("The derived variable value should be " + DerivedVariableProcessorTest.EXPECTED_FORMULA_2_RESULT,
				DerivedVariableProcessorTest.EXPECTED_FORMULA_2_RESULT, result);
	}

	@Test
	public void testFunctions() {
		final String param1 = "number of plots: ";
		final String formula = "fn:concat('" + param1 + "', {PlotSize})";
		final Map<String, Object> terms = this.derivedVariableProcessor.extractTermsFromFormula(formula);
		this.derivedVariableProcessor.fetchTermValuesFromMeasurement(terms, this.createMeasurementRowTestData());

		String result = this.derivedVariableProcessor.evaluateFormula(formula, terms);
		Assert.assertEquals("concat evaluation failed", param1 + TERM_VALUE_3, result);
	}

	@Test
	public void testAggregations() {
		final String formula = "fn:avg({" + TERM_1 + "})";
		final Map<String, Object> terms = new HashMap<>();
		final HashMap<String, List<Object>> data = new HashMap<>();
		final List<Object> termData = new ArrayList<>();
		termData.add(5.5d);
		termData.add(45d);
		termData.add(12.2d);
		data.put(TERM_1, termData);
		String result = this.derivedVariableProcessor.evaluateFormula(formula, terms, data);
		Assert.assertEquals("Should evaluate concat function", "20.9", result);
	}

	@Test(expected = Exception.class)
	public void testSecurityEval() {
		this.derivedVariableProcessor.evaluateFormula("System.exit(0)", new HashMap<String, Object>());
	}

}
