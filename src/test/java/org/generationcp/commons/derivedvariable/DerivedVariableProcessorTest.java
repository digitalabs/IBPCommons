
package org.generationcp.commons.derivedvariable;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.FormulaVariable;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.generationcp.commons.derivedvariable.DerivedVariableUtils.extractInputs;
import static org.generationcp.commons.derivedvariable.DerivedVariableUtils.extractParameters;
import static org.generationcp.commons.derivedvariable.DerivedVariableUtils.extractValues;
import static org.generationcp.commons.derivedvariable.DerivedVariableUtils.getDisplayableFormat;
import static org.generationcp.commons.derivedvariable.DerivedVariableUtils.getEditableFormat;
import static org.generationcp.commons.derivedvariable.DerivedVariableUtils.getStorageFormat;
import static org.generationcp.commons.derivedvariable.DerivedVariableUtils.replaceDelimiters;
import static org.generationcp.commons.derivedvariable.DerivedVariableUtils.wrapTerm;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;

public class DerivedVariableProcessorTest {

	private static final Integer TERM_1 = 51496; // GW_DW_g100grn - Grain weight BY GW DW - Measurement IN G/100grain
	private static final Integer TERM_2 = 50889; // GMoi_NIRS_pct - Grain moisture BY NIRS Moi - Measurement IN %
	private static final Integer TERM_3 = 20358; // PlotArea_m2 - Plot size
	private static final Integer TERM_4_EMPTY_VALUE = 20439; // MRFVInc_Cmp_pct
	private static final Integer DATE_TERM1 = 8630;
	private static final Integer DATE_TERM2 = 8830;
	private static final String TERM_VALUE_1 = "1000";
	private static final String TERM_VALUE_2 = "12.5";
	private static final String TERM_VALUE_3 = "10";
	private static final String DATE_TERM1_VALUE = "20180101";
	private static final String DATE_TERM2_VALUE = "20180201";
	private static final String TERM_NOT_FOUND = "TermNotFound";
	private static final String FORMULA_1 = "({{" + TERM_1 + "}}/100)*((100-{{" + TERM_2 + "}})/(100-12.5))*(10/{{" + TERM_3 + "}})";
	private static final String EXPECTED_FORMULA_1_RESULT = "10";
	private static final String FORMULA_2 = "{{20358}}*6.23";
	private static final String EXPECTED_FORMULA_2_RESULT = "62.3";

	private DerivedVariableProcessor processor;
	private Map<String, Object> parameters;
	private String formula;

	@Before
	public void setUp() {
		this.processor = new DerivedVariableProcessor();
	}


	@Test
	public void testExtractParametersFromFormula() {
		if (this.parameters == null) {
			this.parameters = extractParameters(FORMULA_1);
		}
		Assert.assertNotNull("Terms should be extracted from formula", this.parameters);
		Assert.assertTrue(TERM_1 + " should be one of the extracted parameters",
			this.parameters.containsKey(wrapTerm(String.valueOf(TERM_1))));
		Assert.assertTrue(TERM_2 + " should be one of the extracted parameters",
			this.parameters.containsKey(wrapTerm(String.valueOf(TERM_2))));
		Assert.assertFalse(TERM_NOT_FOUND + " should not be one of the extracted parameters",
			this.parameters.containsKey(wrapTerm(TERM_NOT_FOUND)));
	}

	@Test
	public void testExtractInputsFromFormula() {
		final List<String> inputs = extractInputs(FORMULA_1);

		Assert.assertNotNull("Terms should be extracted from formula", inputs);
		Assert.assertTrue(TERM_1 + " should be one of the extracted parameters", inputs.contains(String.valueOf(TERM_1)));
		Assert.assertTrue(TERM_2 + " should be one of the extracted parameters", inputs.contains(String.valueOf(TERM_2)));
		Assert.assertFalse(TERM_NOT_FOUND + " should not be one of the extracted parameters", inputs.contains(String.valueOf(TERM_NOT_FOUND)));
	}

	@Test
	public void testFetchTermValuesFromMeasurement() throws ParseException {
		if (this.parameters == null) {
			this.parameters = extractParameters(FORMULA_1);
		}

		final Set<String> termMissingData = new HashSet<>();
		extractValues(this.parameters, this.createObservationUnitRowTestData(), this.createMeasurementVariablesMap(), new ArrayList<String>(), new ArrayList<String>());
		Assert.assertNotNull("Terms should not be null", this.parameters);
		for (final Map.Entry<String, Object> entry : this.parameters.entrySet()) {
			final String key = entry.getKey();
			final Object value = entry.getValue();
			Assert.assertNotNull(key + " should have a value", value);
		}
	}

	@Test
	public void testFetchParameterValuesFromMeasurement_MissingData() throws ParseException {
		if (this.parameters == null) {
			this.parameters = extractParameters("{{" + TERM_4_EMPTY_VALUE + "}}");
		}
		final Set<String> termMissingData = new HashSet<>();
		termMissingData.addAll(extractValues(this.parameters, this.createObservationUnitRowTestData(), this.createMeasurementVariablesMap(), new ArrayList<String>(), new ArrayList<String>()));

		Assert.assertThat("Should have missing data", termMissingData, is(not(empty())));
		Assert.assertThat("Should report missing data label", termMissingData.iterator().next(), is(String.valueOf(TERM_4_EMPTY_VALUE)));
	}

	@Test
	public void testFetchParameterValuesFromMeasurement_NullMeasurementRow() throws ParseException {
		final Map<String, Object> testTerms = extractParameters(FORMULA_1);
		extractValues(testTerms, null, this.createMeasurementVariablesMap(), new ArrayList<String>(), new ArrayList<String>());
		for (final Map.Entry<String, Object> entry : testTerms.entrySet()) {
			final String key = entry.getKey();
			final Object value = entry.getValue();
			Assert.assertTrue(key + " should not have a value", "".equals(value));
		}
	}

	@Test
	public void testFetchParameterValuesFromMeasurement_NullMeasurementDataList() throws ParseException {
		final Map<String, Object> testTerms = extractParameters(FORMULA_1);
		final ObservationUnitRow observationUnitRow = new ObservationUnitRow();
		observationUnitRow.setVariables(null);

		final Set<String> termMissingData = new HashSet<>();
		extractValues(testTerms, observationUnitRow, this.createMeasurementVariablesMap(), new ArrayList<String>(), new ArrayList<String>());
		for (final Map.Entry<String, Object> entry : testTerms.entrySet()) {
			final String key = entry.getKey();
			final Object value = entry.getValue();
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
		final String nullFormula = replaceDelimiters(null);
		Assert.assertNull(nullFormula);
	}

	@Test
	public void testGetDisplayableFormat() {
		String formula = "({{" + TERM_1 + "}}-{{" + TERM_2 + "}})/(100-12.5)*10/{{" + TERM_1 + "}}";
		final Map<String, FormulaVariable> variableMap = new HashMap<>();
		final FormulaVariable variable1 = new FormulaVariable();
		variable1.setName("GW_DW_g100grn");
		variableMap.put(String.valueOf(TERM_1), variable1);
		final FormulaVariable variable2 = new FormulaVariable();
		variable2.setName("GMoi_NIRS_pct");
		variableMap.put(String.valueOf(TERM_2), variable2);

		formula = getDisplayableFormat(formula, variableMap);

		Assert.assertThat(
			formula,
			is("(" + variable1.getName() + "-" + variable2.getName() + ")/(100-12.5)*10/" + variable1.getName()));
	}

	@Test
	public void testGetEditableFormat() {
		String formula = "({{" + TERM_1 + "}}-{{" + TERM_2 + "}})/(100-12.5)*10/{{" + TERM_1 + "}}";
		final Map<String, FormulaVariable> variableMap = new HashMap<>();
		final FormulaVariable variable1 = new FormulaVariable();
		variable1.setName("GW_DW_g100grn");
		variableMap.put(String.valueOf(TERM_1), variable1);
		final FormulaVariable variable2 = new FormulaVariable();
		variable2.setName("GMoi_NIRS_pct");
		variableMap.put(String.valueOf(TERM_2), variable2);

		formula = getEditableFormat(formula, variableMap);

		Assert.assertThat(
			formula,
			is("({{" + variable1.getName() + "}}-{{" + variable2.getName() + "}})/(100-12.5)*10/{{" + variable1.getName() + "}}"));
	}

	@Test
	public void testGetStorageFormat() {
		final Map<String, FormulaVariable> variableMap = new HashMap<>();
		final FormulaVariable variable1 = new FormulaVariable();
		variable1.setId(Integer.valueOf(TERM_1));
		variable1.setName("GW_DW_g100grn");
		variableMap.put(variable1.getName(), variable1);
		final FormulaVariable variable2 = new FormulaVariable();
		variable1.setId(Integer.valueOf(TERM_2));
		variable2.setName("GMoi_NIRS_pct");
		variableMap.put(variable2.getName(), variable2);

		String formula = "({{" + variable1.getName() + "}}-{{" + variable2.getName() + "}})/(100-12.5)*10/{{" + variable1.getName() + "}}";

		formula = getStorageFormat(formula, variableMap);

		Assert.assertThat(
			formula,
			is("({{" + variable1.getId() + "}}-{{" + variable2.getId() + "}})/(100-12.5)*10/{{" + variable1.getId() + "}}"));
	}

	@Test
	public void testEvaluateFormula() throws ParseException {
		this.formula = FORMULA_1;
		this.parameters = extractParameters(FORMULA_1);
		extractValues(this.parameters, this.createObservationUnitRowTestData(), this.createMeasurementVariablesMap(), new ArrayList<String>(), new ArrayList<String>());
		this.formula = replaceDelimiters(this.formula);

		String result = this.processor.evaluateFormula(this.formula, this.parameters);
		Assert.assertEquals("The result of " + this.formula + " should be " + EXPECTED_FORMULA_1_RESULT
			+ " but got " + result, EXPECTED_FORMULA_1_RESULT, result);

		this.parameters = extractParameters(FORMULA_2);
		extractValues(this.parameters, this.createObservationUnitRowTestData(), this.createMeasurementVariablesMap(), new ArrayList<String>(), new ArrayList<String>());
		this.formula = replaceDelimiters(FORMULA_2);

		result = this.processor.evaluateFormula(this.formula, this.parameters);
		Assert.assertEquals("Should evaluate formula: " + FORMULA_2, EXPECTED_FORMULA_2_RESULT, result);
	}

	@Test
	public void testEvaluateMapLiterals() {
		String formula = "{ \"mapkey\" : {{TERM1}} + {{TERM2}} }";

		final Map<String, Object> terms = new HashMap<>();
		terms.put(wrapTerm("TERM1"), "TERM1VALUE");
		terms.put(wrapTerm("TERM2"), "TERM2VALUE");

		formula = replaceDelimiters(formula);
		final String result = this.processor.evaluateFormula(formula, terms);
		Assert.assertEquals("Should evaluate map value", "{mapkey=TERM1VALUETERM2VALUE}", result);
	}

	@Test
	public void testEvaluateNumericData() {
		String formula = "{{TERM1}} + {{TERM2}}";

		final Map<String, Object> terms = new HashMap<>();
		terms.put(wrapTerm("TERM1"), 12.3);
		terms.put(wrapTerm("TERM2"), new BigDecimal("2.34"));

		formula = replaceDelimiters(formula);
		final String result = this.processor.evaluateFormula(formula, terms);
		Assert.assertEquals("Should evaluate Numberica data", "14.64", result);
	}

	@Test
	public void testEvaluateDivisionOfWholeNumbers() {
		String formula = "{{TERM1}} / {{TERM2}}";

		final Map<String, Object> terms = new HashMap<>();
		terms.put(wrapTerm("TERM1"), 6);
		terms.put(wrapTerm("TERM2"), 4);

		formula = replaceDelimiters(formula);
		final String result = this.processor.evaluateFormula(formula, terms);
		Assert.assertEquals("1.5", result);
	}

	@Test
	public void testConcatFunction() throws ParseException {
		final String param1 = "number of plots: ";
		String formula = "fn:concat('" + param1 + "', {{" + TERM_3 + "}})";
		final Map<String, Object> terms = extractParameters(formula);
		extractValues(terms, this.createObservationUnitRowTestData(), this.createMeasurementVariablesMap(), new ArrayList<String>(), new ArrayList<String>());

		formula = replaceDelimiters(formula);
		final String result = this.processor.evaluateFormula(formula, terms);
		Assert.assertEquals("concat evaluation failed", param1 + TERM_VALUE_3, result);
	}

	@Test
	public void testDaysDiffFunction() throws ParseException {
		String formula = "fn:daysdiff({{" + DATE_TERM1 + "}}, {{" + DATE_TERM2 + "}})";
		final Map<String, Object> terms = extractParameters(formula);
		extractValues(terms, this.createObservationUnitRowTestData(), this.createMeasurementVariablesMap(), new ArrayList<String>(), new ArrayList<String>());

		formula = replaceDelimiters(formula);
		final String result = this.processor.evaluateFormula(formula, terms);
		Assert.assertEquals("31", result);
	}

	@Test
	public void testDaysDiffFunctionNegativeDifference() throws ParseException {
		// Having later date for first parameter should give negative value
		String formula = "fn:daysdiff({{" + DATE_TERM2 + "}}, {{" + DATE_TERM1 + "}})";
		final Map<String, Object> terms = extractParameters(formula);
		extractValues(terms, this.createObservationUnitRowTestData(), this.createMeasurementVariablesMap(), new ArrayList<String>(), new ArrayList<String>());

		formula = replaceDelimiters(formula);
		final String result = this.processor.evaluateFormula(formula, terms);
		Assert.assertEquals("-31", result);
	}

	@Test
	public void testAggregations() {
		String formula = "fn:avg({{" + TERM_1 + "}})";

		final Map<String, Object> terms = new HashMap<>();

		final HashMap<String, List<Object>> data = new HashMap<>();
		final List<Object> termData = new ArrayList<>();
		termData.add(new BigDecimal(5.5));
		termData.add(new BigDecimal(45));
		termData.add(new BigDecimal(12.2));
		data.put(wrapTerm(String.valueOf(TERM_1)), termData);

		formula = replaceDelimiters(formula);
		this.processor.setData(data);
		String result = this.processor.evaluateFormula(formula, terms);
		Assert.assertEquals("Should evaluate avg function", "20.9", result);

		formula = "fn:avg({{" + TERM_1 + "}}, {{PH_M_cm}})";
		final List<Object> term2Data = new ArrayList<>();
		term2Data.add(new BigDecimal(14.23));
		term2Data.add(new BigDecimal(134.12));
		data.put(wrapTerm("PH_M_cm"), term2Data);

		formula = replaceDelimiters(formula);
		this.processor = new DerivedVariableProcessor();
		this.processor.setData(data);
		result = this.processor.evaluateFormula(formula, terms);
		Assert.assertEquals("Should evaluate avg function", "42.21", result);
	}

	@Test(expected = Exception.class)
	public void testSecurityEval() {
		this.processor.evaluateFormula("System.exit(0)", new HashMap<String, Object>());
	}

	@Test(expected = ParseException.class)
	public void testInvalidDateFormatParsing() throws ParseException {
		final ObservationUnitRow testRow = this.createObservationUnitRowTestData();
		testRow.getVariables().get("DATE_TERM2").setValue("2018-03-31");
		final String formula = "({{" + DATE_TERM2 + "}}/100)";
		final Map<String, Object> terms = extractParameters(formula);
		extractValues(terms, testRow, this.createMeasurementVariablesMap(), new ArrayList<String>(), new ArrayList<String>());
	}

	private ObservationUnitRow createObservationUnitRowTestData() {
		final ObservationUnitRow observationUnitRow = new ObservationUnitRow();
		observationUnitRow.setVariables(this.createObservationUnitDataListTestData());
		observationUnitRow.setEnvironmentVariables(new HashMap<String, ObservationUnitData>());
		return observationUnitRow;
	}

	private Map<Integer, MeasurementVariable> createMeasurementVariablesMap() {
		final Map<Integer, MeasurementVariable> measurementVariablesMap = new HashMap<>();
		measurementVariablesMap.put(TERM_1, this.createMeasurementVariable(TERM_1, "TERM_1",
			DataType.NUMERIC_VARIABLE));
		measurementVariablesMap.put(TERM_2, this.createMeasurementVariable(TERM_2, "TERM_2",
			DataType.NUMERIC_VARIABLE));
		measurementVariablesMap.put(TERM_3, this.createMeasurementVariable(TERM_3, "TERM_3",
			DataType.NUMERIC_VARIABLE));
		measurementVariablesMap.put(TERM_4_EMPTY_VALUE, this.createMeasurementVariable(TERM_4_EMPTY_VALUE, "TERM_4",
			DataType.NUMERIC_VARIABLE));
		measurementVariablesMap.put(DATE_TERM1, this.createMeasurementVariable(DATE_TERM1, "DATE_TERM1",
			DataType.DATE_TIME_VARIABLE));
		measurementVariablesMap.put(DATE_TERM2, this.createMeasurementVariable(DATE_TERM2, "DATE_TERM2",
			DataType.DATE_TIME_VARIABLE));
		return measurementVariablesMap;

	}

	private MeasurementVariable createMeasurementVariable(final int variableId, final String variableName, final DataType dataType) {
		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(variableId);
		measurementVariable.setName(variableName);
		measurementVariable.setDataTypeId(dataType.getId());
		measurementVariable.setLabel(String.valueOf(variableId));
		return measurementVariable;
	}

	private Map<String, ObservationUnitData> createObservationUnitDataListTestData() {
		final Map<String, ObservationUnitData> observationUnitDataMap = new HashMap<>();
		observationUnitDataMap.put("TERM_1", this.createObservationUnitDataTestData(TERM_1, TERM_VALUE_1, null));
		observationUnitDataMap.put("TERM_2", this.createObservationUnitDataTestData(TERM_2, TERM_VALUE_2, null));
		observationUnitDataMap.put("TERM_3", this.createObservationUnitDataTestData(TERM_3, TERM_VALUE_3, null));
		observationUnitDataMap.put("TERM_4", this.createObservationUnitDataTestData(TERM_4_EMPTY_VALUE, "", ""));
		final ObservationUnitData dateData1 = this.createObservationUnitDataTestData(DATE_TERM1, DATE_TERM1_VALUE, null);
		observationUnitDataMap.put("DATE_TERM1", dateData1);
		final ObservationUnitData dateData2 = this.createObservationUnitDataTestData(DATE_TERM2, DATE_TERM2_VALUE, null);
		observationUnitDataMap.put("DATE_TERM2", dateData2);
		return observationUnitDataMap;
	}

	private ObservationUnitData createObservationUnitDataTestData(final int variableId, final String value, final String cValueId) {
		final ObservationUnitData observationUnitData = new ObservationUnitData();
		observationUnitData.setVariableId(variableId);
		observationUnitData.setValue(value);
		observationUnitData.setCategoricalValueId(!StringUtils.isBlank(cValueId) ? Integer.valueOf(cValueId) : null);
		return observationUnitData;
	}

}
