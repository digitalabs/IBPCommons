
package org.generationcp.commons.derivedvariable;

import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.generationcp.commons.derivedvariable.DerivedVariableProcessor.wrapTerm;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;

public class DerivedVariableProcessorTest {

	private static final String TERM_1 = "51496"; // GW_DW_g100grn - Grain weight BY GW DW - Measurement IN G/100grain
	private static final String TERM_2 = "50889"; // GMoi_NIRS_pct - Grain moisture BY NIRS Moi - Measurement IN %
	private static final String TERM_3 = "20358"; // PlotArea_m2 - Plot size
	private static final String TERM_4_EMPTY_VALUE = "20439"; // MRFVInc_Cmp_pct
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
		measurementDataList.add(this.createMeasurementDataTestData(DerivedVariableProcessorTest.TERM_4_EMPTY_VALUE, "", ""));
		return measurementDataList;
	}

	private MeasurementData createMeasurementDataTestData(String label, String value, String cValueId) {
		MeasurementData measurementData = new MeasurementData();
		measurementData.setLabel(label);
		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(Integer.valueOf(label));
		measurementData.setMeasurementVariable(measurementVariable);
		measurementData.setValue(value);
		measurementData.setcValueId(cValueId);
		return measurementData;
	}

	@Test
	public void testExtractTermsFromFormula() {
		if (this.terms == null) {
			this.terms = this.derivedVariableProcessor.extractTerms(DerivedVariableProcessorTest.FORMULA_1);
		}
		Assert.assertNotNull("Terms should be extracted from formula", this.terms);
		Assert.assertTrue(
			DerivedVariableProcessorTest.TERM_1 + " should be one of the extracted terms",
			this.terms.containsKey(wrapTerm(DerivedVariableProcessorTest.TERM_1)));
		Assert.assertTrue(
			DerivedVariableProcessorTest.TERM_2 + " should be one of the extracted terms",
			this.terms.containsKey(wrapTerm(DerivedVariableProcessorTest.TERM_2.trim())));
		Assert.assertFalse(
			DerivedVariableProcessorTest.TERM_NOT_FOUND + " should not be one of the extracted terms",
			this.terms.containsKey(wrapTerm(DerivedVariableProcessorTest.TERM_NOT_FOUND)));
	}

	@Test
	public void testFetchTermValuesFromMeasurement() {
		if (this.terms == null) {
			this.terms = this.derivedVariableProcessor.extractTerms(DerivedVariableProcessorTest.FORMULA_1);
		}
		this.derivedVariableProcessor.extractValues(this.terms, this.createMeasurementRowTestData());
		Assert.assertNotNull("Terms should not be null", this.terms);
		for (Map.Entry<String, Object> entry : this.terms.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			Assert.assertNotNull(key + " should have a value", value);
		}
	}

	@Test
	public void testFetchTermValuesFromMeasurement_MissingData() {
		if (this.terms == null) {
			this.terms = this.derivedVariableProcessor.extractTerms("{{" + TERM_4_EMPTY_VALUE + "}}");
		}
		final Set<String> termMissingData = new HashSet<>();

		this.derivedVariableProcessor.extractValues(this.terms, this.createMeasurementRowTestData(), termMissingData);

		Assert.assertThat("Should have missing data", termMissingData, is(not(empty())));
		Assert.assertThat("Should report missing data label", termMissingData.iterator().next(), is(TERM_4_EMPTY_VALUE));
	}

	@Test
	public void testFetchTermValuesFromMeasurement_NullMeasurementRow() {
		Map<String, Object> testTerms = this.derivedVariableProcessor.extractTerms(DerivedVariableProcessorTest.FORMULA_1);
		this.derivedVariableProcessor.extractValues(testTerms, null);
		for (Map.Entry<String, Object> entry : testTerms.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			Assert.assertTrue(key + " should not have a value", "".equals(value));
		}
	}

	@Test
	public void testFetchTermValuesFromMeasurement_NullMeasurementDataList() {
		Map<String, Object> testTerms = this.derivedVariableProcessor.extractTerms(DerivedVariableProcessorTest.FORMULA_1);
		MeasurementRow measurementRow = new MeasurementRow();
		measurementRow.setDataList(null);
		this.derivedVariableProcessor.extractValues(testTerms, measurementRow);
		for (Map.Entry<String, Object> entry : testTerms.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			Assert.assertTrue(key + " should not have a value", "".equals(value));
		}
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
		this.terms = this.derivedVariableProcessor.extractTerms(DerivedVariableProcessorTest.FORMULA_1);
		this.derivedVariableProcessor.extractValues(this.terms, this.createMeasurementRowTestData());
		String result = this.derivedVariableProcessor.evaluateFormula(this.formula, this.terms);
		Assert.assertEquals("The result of " + this.formula + " should be " + DerivedVariableProcessorTest.EXPECTED_FORMULA_1_RESULT
				+ " but got " + result, DerivedVariableProcessorTest.EXPECTED_FORMULA_1_RESULT, result);

		this.terms = this.derivedVariableProcessor.extractTerms(DerivedVariableProcessorTest.FORMULA_1);
		this.derivedVariableProcessor.extractValues(this.terms, this.createMeasurementRowTestData());
		result = this.derivedVariableProcessor.evaluateFormula(DerivedVariableProcessorTest.FORMULA_2, this.terms);
		Assert.assertEquals("Should evaluate formula: " + FORMULA_2, DerivedVariableProcessorTest.EXPECTED_FORMULA_2_RESULT, result);
	}

	@Test
	public void testEvaluateMapLiterals() {
		String formula = "{ \"mapkey\" : {{TERM1}} + {{TERM2}} }";

		final Map<String, Object> terms = new HashMap<>();
		terms.put(wrapTerm("TERM1"), "TERM1VALUE");
		terms.put(wrapTerm("TERM2"), "TERM2VALUE");

		String result = this.derivedVariableProcessor.evaluateFormula(formula, terms);
		Assert.assertEquals("Should evaluate map value", "{mapkey=TERM1VALUETERM2VALUE}", result);
	}
	
	@Test
	public void testEvaluateNumericData() {
		String formula = "{{TERM1}} + {{TERM2}}";

		final Map<String, Object> terms = new HashMap<>();
		terms.put(wrapTerm("TERM1"), 12.3);
		terms.put(wrapTerm("TERM2"), new BigDecimal("2.34"));

		String result = this.derivedVariableProcessor.evaluateFormula(formula, terms);
		Assert.assertEquals("Should evaluate Numberica data", "14.64", result);
	}

	@Test
	public void testFunctions() {
		final String param1 = "number of plots: ";
		final String formula = "fn:concat('" + param1 + "', {{" + TERM_3 + "}})";
		final Map<String, Object> terms = this.derivedVariableProcessor.extractTerms(formula);
		this.derivedVariableProcessor.extractValues(terms, this.createMeasurementRowTestData());

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
		data.put(wrapTerm(TERM_1), termData);

		String result = this.derivedVariableProcessor.evaluateFormula(formula, terms, data);
		Assert.assertEquals("Should evaluate concat function", "20.9", result);

		formula = "fn:avg([{{" + TERM_1 + "}}, {{PH_M_cm}}])";
		final List<Object> term2Data = new ArrayList<>();
		term2Data.add(14.23d);
		term2Data.add(134.12);
		data.put(wrapTerm("PH_M_cm"), term2Data);

		result = this.derivedVariableProcessor.evaluateFormula(formula, terms, data);
		Assert.assertEquals("Should evaluate concat function", "42.21", result);
	}

	@Test(expected = Exception.class)
	public void testSecurityEval() {
		this.derivedVariableProcessor.evaluateFormula("System.exit(0)", new HashMap<String, Object>());
	}

}
