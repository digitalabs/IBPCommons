
package org.generationcp.commons.derivedvariable;

import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DerivedVariableProcessorTest {

	private static final String TERM_1 = "51496"; // GW_DW_g100grn - Grain weight BY GW DW - Measurement IN G/100grain
	private static final String TERM_2 = "50889"; // GMoi_NIRS_pct - Grain moisture BY NIRS Moi - Measurement IN %
	private static final String TERM_3 = "20358"; // PlotArea_m2 - Plot size
	private static final String TERM_VALUE_1 = "1000";
	private static final String TERM_VALUE_2 = "12.5";
	private static final String TERM_VALUE_3 = "10";
	private static final String TERM_NOT_FOUND = "TermNotFound";
	private static final String FORMULA_1 = "({{" + DerivedVariableProcessorTest.TERM_1 + "}}/100)*((100-{{"
			+ DerivedVariableProcessorTest.TERM_2 + "}})/(100-12.5))*(10/{{" + DerivedVariableProcessorTest.TERM_3 + "}})";
	private static final String EXPECTED_FORMULA_1_RESULT = "10";
	private static final String FORMULA_2 = "{{20358}}*6.23";
	private static final String EXPECTED_FORMULA_2_RESULT = "62.3";
	private DerivedVariableProcessor derivedVariableProcessor;
	private Map<String, Object> terms;
	private String formula;

	@Before
	public void setUp() {
		this.derivedVariableProcessor = new DerivedVariableProcessor();
	}

	@Test
	public void testExtractTermsFromFormula() {
		if (this.terms == null) {
			this.terms = this.derivedVariableProcessor.extractTermsFromFormula(DerivedVariableProcessorTest.FORMULA_1);
		}
		Assert.assertNotNull("Terms should be extracted from formula", this.terms);
		Assert.assertTrue(
			DerivedVariableProcessorTest.TERM_1 + " should be one of the extracted terms",
			this.terms.containsKey(
				DerivedVariableProcessor.TERM_INTERNAL_WRAPPER
					+ DerivedVariableProcessorTest.TERM_1 + DerivedVariableProcessor.TERM_INTERNAL_WRAPPER));
		Assert.assertTrue(
			DerivedVariableProcessorTest.TERM_2 + " should be one of the extracted terms",
			this.terms.containsKey(DerivedVariableProcessor.TERM_INTERNAL_WRAPPER + DerivedVariableProcessorTest.TERM_2.trim()
				+ DerivedVariableProcessor.TERM_INTERNAL_WRAPPER));
		Assert.assertFalse(
			DerivedVariableProcessorTest.TERM_NOT_FOUND + " should not be one of the extracted terms",
			this.terms.containsKey(DerivedVariableProcessor.TERM_INTERNAL_WRAPPER + DerivedVariableProcessorTest.TERM_NOT_FOUND
				+ DerivedVariableProcessor.TERM_INTERNAL_WRAPPER));
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
		measurementDataList.add(this.createMeasurementDataTestData(DerivedVariableProcessorTest.TERM_2,
				DerivedVariableProcessorTest.TERM_VALUE_2, null));
		measurementDataList.add(this.createMeasurementDataTestData(DerivedVariableProcessorTest.TERM_3,
				DerivedVariableProcessorTest.TERM_VALUE_3, null));
		return measurementDataList;
	}

	private MeasurementData createMeasurementDataTestData(String label, String value, String cValueId) {
		MeasurementData measurementData = new MeasurementData();
		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(Integer.valueOf(label));
		measurementData.setMeasurementVariable(measurementVariable);
		measurementData.setValue(value);
		measurementData.setcValueId(cValueId);
		return measurementData;
	}

	@Test
	public void testRemoveCurlyBracesFromFormula() {
		this.formula = this.derivedVariableProcessor.replaceBraces(DerivedVariableProcessorTest.FORMULA_1);
		Assert.assertFalse(this.formula.contains("{{"));
		Assert.assertFalse(this.formula.contains("}}"));
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
	public void testEvaluateMapLiterals() {
		String formula = "{ \"mapkey\" : {{TERM1}} + {{TERM2}} }";

		final Map<String, Object> terms = new HashMap<>();
		terms.put(DerivedVariableProcessor.TERM_INTERNAL_WRAPPER + "TERM1" + DerivedVariableProcessor.TERM_INTERNAL_WRAPPER, "TERM1VALUE");
		terms.put(DerivedVariableProcessor.TERM_INTERNAL_WRAPPER + "TERM2" + DerivedVariableProcessor.TERM_INTERNAL_WRAPPER, "TERM2VALUE");

		String result = this.derivedVariableProcessor.evaluateFormula(formula, terms);
		Assert.assertEquals("Should evaluate map value", "{mapkey=TERM1VALUETERM2VALUE}", result);
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
		final String formula = "fn:concat('" + param1 + "', {{20358}})";
		final Map<String, Object> terms = this.derivedVariableProcessor.extractTermsFromFormula(formula);
		this.derivedVariableProcessor.fetchTermValuesFromMeasurement(terms, this.createMeasurementRowTestData());

		String result = this.derivedVariableProcessor.evaluateFormula(formula, terms);
		Assert.assertEquals("concat evaluation failed", param1 + TERM_VALUE_3, result);
	}

	@Test
	public void testAggregations() {
		String formula = "fn:avg({{" + TERM_1 + "}})";

		final Map<String, Object> terms = new HashMap<>();

		final HashMap<String, List<Object>> data = new HashMap<>();
		final List<Object> termData = new ArrayList<>();
		termData.add(5.5d);
		termData.add(45d);
		termData.add(12.2d);
		data.put(DerivedVariableProcessor.TERM_INTERNAL_WRAPPER + TERM_1 + DerivedVariableProcessor.TERM_INTERNAL_WRAPPER, termData);

		String result = this.derivedVariableProcessor.evaluateFormula(formula, terms, data);
		Assert.assertEquals("Should evaluate concat function", "20.9", result);

		formula = "fn:avg([{{" + TERM_1 + "}}, {{PH_M_cm}}])";
		final List<Object> term2Data = new ArrayList<>();
		term2Data.add(14.23d);
		term2Data.add(134.12);
		data.put(DerivedVariableProcessor.TERM_INTERNAL_WRAPPER + "PH_M_cm" + DerivedVariableProcessor.TERM_INTERNAL_WRAPPER, term2Data);

		result = this.derivedVariableProcessor.evaluateFormula(formula, terms, data);
		Assert.assertEquals("Should evaluate concat function", "42.21", result);
	}

	@Test(expected = Exception.class)
	public void testSecurityEval() {
		this.derivedVariableProcessor.evaluateFormula("System.exit(0)", new HashMap<String, Object>());
	}

}
