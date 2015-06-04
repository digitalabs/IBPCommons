
package org.generationcp.commons.derivedvariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DerivedVariableProcessorTest {

	private static final String TERM_1 = "GrainWghtg";
	private static final String TERM_2 = "Moisture";
	private static final String TERM_3 = "\"PlotSize\"";
	private static final String UNMODIFIED_TERM_2 = "% Moisture";
	private static final String TERM_VALUE_1 = "1000";
	private static final String TERM_VALUE_2 = "12.5";
	private static final String TERM_VALUE_3 = "10";
	private static final String TERM_NOT_FOUND = "TermNotFound";
	private static final String FORMULA = "(\"{" + DerivedVariableProcessorTest.TERM_1 + "}\"/100)*((100-{"
			+ DerivedVariableProcessorTest.TERM_2 + "})/(100-12.5))*(10/{" + DerivedVariableProcessorTest.TERM_3 + "})";
	private static final String EXPECTED_FORMULA_RESULT = "10";
	private static final String FORMULA_2 = "{PlotSize}*6.23";
	private static final String EXPECTED_FORMULA_2_RESULT = "62.3";
	private DerivedVariableProcessor derivedVariableProcessor;
	private Map<String, String> terms;
	private String updatedFormula;

	@Before
	public void setUp() {
		this.derivedVariableProcessor = DerivedVariableProcessor.getInstance();
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
			this.terms = this.derivedVariableProcessor.extractTermsFromFormula(DerivedVariableProcessorTest.FORMULA);
		}
		Assert.assertNotNull("Terms should be extracted from formula", this.terms);
		Assert.assertTrue(DerivedVariableProcessorTest.TERM_1 + " should be one of the extracted terms",
				this.terms.containsKey(DerivedVariableProcessorTest.TERM_1));
		Assert.assertTrue(DerivedVariableProcessorTest.TERM_2 + " should be one of the extracted terms",
				this.terms.containsKey(DerivedVariableProcessorTest.TERM_2));
		Assert.assertFalse(DerivedVariableProcessorTest.TERM_NOT_FOUND + " should not be one of the extracted terms",
				this.terms.containsKey(DerivedVariableProcessorTest.TERM_NOT_FOUND));
	}

	@Test
	public void testFetchTermValuesFromMeasurement() {
		if (this.terms == null) {
			this.terms = this.derivedVariableProcessor.extractTermsFromFormula(DerivedVariableProcessorTest.FORMULA);
		}
		this.derivedVariableProcessor.fetchTermValuesFromMeasurement(this.terms, this.createMeasurementRowTestData());
		Assert.assertNotNull("Terms should not be null", this.terms);
		for (Map.Entry<String, String> entry : this.terms.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			Assert.assertNotNull(key + " should have a value", value);
		}
	}

	@Test
	public void testFetchTermValuesFromMeasurement_NullMeasurementRow() {
		Map<String, String> testTerms = this.derivedVariableProcessor.extractTermsFromFormula(DerivedVariableProcessorTest.FORMULA);
		this.derivedVariableProcessor.fetchTermValuesFromMeasurement(testTerms, null);
		for (Map.Entry<String, String> entry : testTerms.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			Assert.assertNull(key + " should not have a value", value);
		}
	}

	@Test
	public void testFetchTermValuesFromMeasurement_NullMeasurementDataList() {
		Map<String, String> testTerms = this.derivedVariableProcessor.extractTermsFromFormula(DerivedVariableProcessorTest.FORMULA);
		MeasurementRow measurementRow = new MeasurementRow();
		measurementRow.setDataList(null);
		this.derivedVariableProcessor.fetchTermValuesFromMeasurement(testTerms, measurementRow);
		for (Map.Entry<String, String> entry : testTerms.entrySet()) {
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
		List<MeasurementData> measurementDataList = new ArrayList<MeasurementData>();
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
		if (this.updatedFormula == null) {
			this.updatedFormula = this.derivedVariableProcessor.removeCurlyBracesFromFormula(DerivedVariableProcessorTest.FORMULA);
		}
		Assert.assertFalse(this.updatedFormula.contains("{"));
		Assert.assertFalse(this.updatedFormula.contains("}"));
	}

	@Test
	public void testRemoveCurlyBracesFromFormula_NullFormula() {
		String nullFormula = this.derivedVariableProcessor.removeCurlyBracesFromFormula(null);
		Assert.assertNull(nullFormula);
	}

	@Test
	public void testEvaluateFormula() {
		if (this.updatedFormula == null) {
			this.updatedFormula = this.derivedVariableProcessor.removeCurlyBracesFromFormula(DerivedVariableProcessorTest.FORMULA);
		}
		if (this.terms == null) {
			this.terms = this.derivedVariableProcessor.extractTermsFromFormula(DerivedVariableProcessorTest.FORMULA);
		}
		if (this.terms.values().iterator().next() == null) {
			this.derivedVariableProcessor.fetchTermValuesFromMeasurement(this.terms, this.createMeasurementRowTestData());
		}
		String result = this.derivedVariableProcessor.evaluateFormula(this.updatedFormula, this.terms);
		Assert.assertEquals("The result of " + this.updatedFormula + " should be " + DerivedVariableProcessorTest.EXPECTED_FORMULA_RESULT
				+ " but got " + result, DerivedVariableProcessorTest.EXPECTED_FORMULA_RESULT, result);
	}

	@Test
	public void testGetDerivedVariableValue() {
		String result =
				this.derivedVariableProcessor.getDerivedVariableValue(DerivedVariableProcessorTest.FORMULA_2,
						this.createMeasurementRowTestData());
		Assert.assertEquals("The derived variable value should be " + DerivedVariableProcessorTest.EXPECTED_FORMULA_2_RESULT,
				DerivedVariableProcessorTest.EXPECTED_FORMULA_2_RESULT, result);
	}
}
