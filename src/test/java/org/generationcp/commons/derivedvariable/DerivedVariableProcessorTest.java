package org.generationcp.commons.derivedvariable;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.junit.Before;
import org.junit.Test;

public class DerivedVariableProcessorTest {

	private static final String TERM_1 = "GY_kg_ha__DryWB";
	private static final String TERM_2 = "EarW_kg__FieldWB";
	private static final String TERM_3 = "WGN_pct";
	private static final String UNMODIFIED_TERM_2 = "EarW_kg __FieldWB";
	private static final String TERM_VALUE_1 = "10";
	private static final String TERM_VALUE_2 = "5";
	private static final String TERM_VALUE_3 = "2";
	private static final String TERM_NOT_FOUND = "TermNotFound";
	private static final String FORMULA = "100*({"+TERM_1+"}/{"+TERM_2+"})";
	private static final double EXPECTED_FORMULA_RESULT = 200;
	private static final String FORMULA_2 = "{WGN_pct}*6";
	private static final int EXPECTED_FORMULA_2_RESULT = 12;
	private DerivedVariableProcessor derivedVariableProcessor;
	private Map<String,String> terms;
	private String updatedFormula;
	
	@Before
	public void setUp() {
		derivedVariableProcessor = DerivedVariableProcessor.getInstance();
	}
	
	@Test
	public void testRemoveAllWhiteSpaces() {
		String term = derivedVariableProcessor.removeAllWhiteSpaces(UNMODIFIED_TERM_2);
		assertEquals(UNMODIFIED_TERM_2+" should become "+TERM_2,TERM_2,term);
		
		String nullTerm = derivedVariableProcessor.removeAllWhiteSpaces(null);
		assertEquals(null+" should become "+null,null,nullTerm);
	}
	
	@Test
	public void testExtractTermsFromFormula() {
		if(terms==null) {
			terms = derivedVariableProcessor.extractTermsFromFormula(FORMULA);
		}
		assertNotNull("Terms should be extracted from formula",terms);
		assertTrue(TERM_1+" should be one of the extracted terms",terms.containsKey(TERM_1));
		assertTrue(TERM_2+" should be one of the extracted terms",terms.containsKey(TERM_2));
		assertFalse(TERM_NOT_FOUND+" should not be one of the extracted terms",terms.containsKey(TERM_NOT_FOUND));
	}
	
	@Test
	public void testFetchTermValuesFromMeasurement() {
		if(terms==null) {
			terms = derivedVariableProcessor.extractTermsFromFormula(FORMULA);
		}
		derivedVariableProcessor.fetchTermValuesFromMeasurement(terms,
				createMeasurementRowTestData());
		assertNotNull("Terms should not be null",terms);
		for (Map.Entry<String, String> entry : terms.entrySet()) {
		    String key = entry.getKey();
		    Object value = entry.getValue();
		    assertNotNull(key+" should have a value",value);
		}
	}
	
	@Test
	public void testFetchTermValuesFromMeasurement_NullMeasurementRow() {
		Map<String,String> testTerms = derivedVariableProcessor.extractTermsFromFormula(FORMULA);
		derivedVariableProcessor.fetchTermValuesFromMeasurement(testTerms,null);
		for (Map.Entry<String, String> entry : testTerms.entrySet()) {
		    String key = entry.getKey();
		    Object value = entry.getValue();
		    assertNull(key+" should not have a value",value);
		}
	}
	
	@Test
	public void testFetchTermValuesFromMeasurement_NullMeasurementDataList() {
		Map<String,String> testTerms = derivedVariableProcessor.extractTermsFromFormula(FORMULA);
		MeasurementRow measurementRow = new MeasurementRow();
		measurementRow.setDataList(null);
		derivedVariableProcessor.fetchTermValuesFromMeasurement(testTerms,measurementRow);
		for (Map.Entry<String, String> entry : testTerms.entrySet()) {
		    String key = entry.getKey();
		    Object value = entry.getValue();
		    assertNull(key+" should not have a value",value);
		}
	}
	
	private MeasurementRow createMeasurementRowTestData() {
		MeasurementRow measurementRow = new MeasurementRow();
		measurementRow.setDataList(createMeasurementDataListTestData());
		return measurementRow;
	}

	private List<MeasurementData> createMeasurementDataListTestData() {
		List<MeasurementData> measurementDataList = new ArrayList<MeasurementData>();
		measurementDataList.add(createMeasurementDataTestData(TERM_1,TERM_VALUE_1,null));
		measurementDataList.add(createMeasurementDataTestData(UNMODIFIED_TERM_2,null,TERM_VALUE_2));
		measurementDataList.add(createMeasurementDataTestData(TERM_3,TERM_VALUE_3,null));
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
		if(updatedFormula==null) {
			updatedFormula = derivedVariableProcessor.removeCurlyBracesFromFormula(FORMULA);
		}
		assertFalse(updatedFormula.contains("{"));
		assertFalse(updatedFormula.contains("}"));
	}
	
	@Test
	public void testRemoveCurlyBracesFromFormula_NullFormula() {
		String nullFormula = derivedVariableProcessor.removeCurlyBracesFromFormula(null);
		assertNull(nullFormula);
	}
	
	@Test
	public void testEvaluateFormula() {
		if(updatedFormula==null) {
			updatedFormula = derivedVariableProcessor.removeCurlyBracesFromFormula(FORMULA);
		}
		if(terms==null) {
			terms = derivedVariableProcessor.extractTermsFromFormula(FORMULA);
		}
		if (terms.values().iterator().next()==null) {
			derivedVariableProcessor.fetchTermValuesFromMeasurement(terms,
					createMeasurementRowTestData());
		}
		String result = derivedVariableProcessor.evaluateFormula(updatedFormula,terms);
		assertTrue("The result of "+updatedFormula+" should be "+EXPECTED_FORMULA_RESULT,
				EXPECTED_FORMULA_RESULT == Double.parseDouble(result));
	}
	
	@Test
	public void testGetDerivedVariableValue() {
		String result = derivedVariableProcessor.getDerivedVariableValue(FORMULA_2, 
				createMeasurementRowTestData());
		assertTrue("The derived variable value should be " + EXPECTED_FORMULA_2_RESULT,
				EXPECTED_FORMULA_2_RESULT == Integer.parseInt(result));
	}
}
