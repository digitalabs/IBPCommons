
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

import static org.generationcp.commons.derivedvariable.DerivedVariableUtils.extractTerms;
import static org.generationcp.commons.derivedvariable.DerivedVariableUtils.extractValues;
import static org.generationcp.commons.derivedvariable.DerivedVariableUtils.replaceDelimiters;
import static org.generationcp.commons.derivedvariable.DerivedVariableUtils.wrapTerm;
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
	private static final String FORMULA_1 = "({{" + TERM_1 + "}}/100)*((100-{{" + TERM_2 + "}})/(100-12.5))*(10/{{" + TERM_3 + "}})";
	private static final String EXPECTED_FORMULA_1_RESULT = "10";
	private static final String FORMULA_2 = "{{20358}}*6.23";
	private static final String EXPECTED_FORMULA_2_RESULT = "62.3";

	private DerivedVariableProcessor processor;
	private Map<String, Object> terms;
	private String formula;

	@Before
	public void setUp() {
		this.processor = new DerivedVariableProcessor();
	}

	private MeasurementRow createMeasurementRowTestData() {
		MeasurementRow measurementRow = new MeasurementRow();
		measurementRow.setDataList(this.createMeasurementDataListTestData());
		return measurementRow;
	}

	private List<MeasurementData> createMeasurementDataListTestData() {
		List<MeasurementData> measurementDataList = new ArrayList<>();
		measurementDataList.add(this.createMeasurementDataTestData(TERM_1, TERM_VALUE_1, null));
		measurementDataList.add(this.createMeasurementDataTestData(TERM_2, TERM_VALUE_2, null));
		measurementDataList.add(this.createMeasurementDataTestData(TERM_3, TERM_VALUE_3, null));
		measurementDataList.add(this.createMeasurementDataTestData(TERM_4_EMPTY_VALUE, "", ""));
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
			this.terms = extractTerms(FORMULA_1);
		}
		Assert.assertNotNull("Terms should be extracted from formula", this.terms);
		Assert.assertTrue(TERM_1 + " should be one of the extracted terms", this.terms.containsKey(wrapTerm(TERM_1)));
		Assert.assertTrue(TERM_2 + " should be one of the extracted terms", this.terms.containsKey(wrapTerm(TERM_2.trim())));
		Assert.assertFalse(TERM_NOT_FOUND + " should not be one of the extracted terms", this.terms.containsKey(wrapTerm(TERM_NOT_FOUND)));
	}

	@Test
	public void testFetchTermValuesFromMeasurement() {
		if (this.terms == null) {
			this.terms = extractTerms(FORMULA_1);
		}
		extractValues(this.terms, this.createMeasurementRowTestData());
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
			this.terms = extractTerms("{{" + TERM_4_EMPTY_VALUE + "}}");
		}
		final Set<String> termMissingData = new HashSet<>();

		extractValues(this.terms, this.createMeasurementRowTestData(), termMissingData);

		Assert.assertThat("Should have missing data", termMissingData, is(not(empty())));
		Assert.assertThat("Should report missing data label", termMissingData.iterator().next(), is(TERM_4_EMPTY_VALUE));
	}

	@Test
	public void testFetchTermValuesFromMeasurement_NullMeasurementRow() {
		Map<String, Object> testTerms = extractTerms(FORMULA_1);
		extractValues(testTerms, null);
		for (Map.Entry<String, Object> entry : testTerms.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			Assert.assertTrue(key + " should not have a value", "".equals(value));
		}
	}

	@Test
	public void testFetchTermValuesFromMeasurement_NullMeasurementDataList() {
		Map<String, Object> testTerms = extractTerms(FORMULA_1);
		MeasurementRow measurementRow = new MeasurementRow();
		measurementRow.setDataList(null);
		extractValues(testTerms, measurementRow);
		for (Map.Entry<String, Object> entry : testTerms.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			Assert.assertTrue(key + " should not have a value", "".equals(value));
		}
	}

	@Test
	public void testRemoveCurlyBracesFromFormula() {
		this.formula = replaceDelimiters(FORMULA_1);
		Assert.assertFalse(this.formula.contains("{{"));
		Assert.assertFalse(this.formula.contains("}}"));
	}

	@Test
	public void testRemoveCurlyBracesFromFormula_NullFormula() {
		String nullFormula = replaceDelimiters(null);
		Assert.assertNull(nullFormula);
	}

	@Test
	public void testEvaluateFormula() {
		this.formula = FORMULA_1;
		this.terms = extractTerms(FORMULA_1);
		extractValues(this.terms, this.createMeasurementRowTestData());
		this.formula = replaceDelimiters(this.formula);

		String result = this.processor.evaluateFormula(this.formula, this.terms);
		Assert.assertEquals("The result of " + this.formula + " should be " + EXPECTED_FORMULA_1_RESULT
			+ " but got " + result, EXPECTED_FORMULA_1_RESULT, result);

		this.terms = extractTerms(FORMULA_2);
		extractValues(this.terms, this.createMeasurementRowTestData());
		this.formula = replaceDelimiters(FORMULA_2);

		result = this.processor.evaluateFormula(this.formula, this.terms);
		Assert.assertEquals("Should evaluate formula: " + FORMULA_2, EXPECTED_FORMULA_2_RESULT, result);
	}

	@Test
	public void testEvaluateMapLiterals() {
		String formula = "{ \"mapkey\" : {{TERM1}} + {{TERM2}} }";

		final Map<String, Object> terms = new HashMap<>();
		terms.put(wrapTerm("TERM1"), "TERM1VALUE");
		terms.put(wrapTerm("TERM2"), "TERM2VALUE");

		formula = replaceDelimiters(formula);
		String result = this.processor.evaluateFormula(formula, terms);
		Assert.assertEquals("Should evaluate map value", "{mapkey=TERM1VALUETERM2VALUE}", result);
	}

	@Test
	public void testEvaluateNumericData() {
		String formula = "{{TERM1}} + {{TERM2}}";

		final Map<String, Object> terms = new HashMap<>();
		terms.put(wrapTerm("TERM1"), 12.3);
		terms.put(wrapTerm("TERM2"), new BigDecimal("2.34"));

		formula = replaceDelimiters(formula);
		String result = this.processor.evaluateFormula(formula, terms);
		Assert.assertEquals("Should evaluate Numberica data", "14.64", result);
	}

	@Test
	public void testFunctions() {
		final String param1 = "number of plots: ";
		String formula = "fn:concat('" + param1 + "', {{" + TERM_3 + "}})";
		final Map<String, Object> terms = extractTerms(formula);
		extractValues(terms, this.createMeasurementRowTestData());

		formula = replaceDelimiters(formula);
		String result = this.processor.evaluateFormula(formula, terms);
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

		formula = replaceDelimiters(formula);
		this.processor.setData(data);
		String result = this.processor.evaluateFormula(formula, terms);
		Assert.assertEquals("Should evaluate concat function", "20.9", result);

		formula = "fn:avg([{{" + TERM_1 + "}}, {{PH_M_cm}}])";
		final List<Object> term2Data = new ArrayList<>();
		term2Data.add(14.23d);
		term2Data.add(134.12);
		data.put(wrapTerm("PH_M_cm"), term2Data);

		formula = replaceDelimiters(formula);
		this.processor = new DerivedVariableProcessor();
		this.processor.setData(data);
		result = this.processor.evaluateFormula(formula, terms);
		Assert.assertEquals("Should evaluate concat function", "42.21", result);
	}

	@Test(expected = Exception.class)
	public void testSecurityEval() {
		this.processor.evaluateFormula("System.exit(0)", new HashMap<String, Object>());
	}

}
