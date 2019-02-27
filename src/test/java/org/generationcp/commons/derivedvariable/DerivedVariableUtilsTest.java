package org.generationcp.commons.derivedvariable;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.exolab.castor.types.Date;
import org.generationcp.commons.util.DateUtil;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class DerivedVariableUtilsTest {

	@Test
	public void testExtractValuesFromObservationUnitRowVariableIsCategorical() throws ParseException {

		final String variableName = RandomStringUtils.randomAlphanumeric(10);
		final int variableTermid = RandomUtils.nextInt();
		final int categoricalValueId = RandomUtils.nextInt();

		final Map<String, Object> parameters = new HashMap<>();
		final Set<String> termMissingData = new HashSet<>();
		parameters.put(DerivedVariableUtils.wrapTerm(String.valueOf(variableTermid)), null);

		final ObservationUnitRow observationUnitRow = new ObservationUnitRow();
		final Map<String, ObservationUnitData> observationUnitDataMap = new HashMap<>();
		observationUnitDataMap.put(variableName, this.createObservationUnitDataTestData(variableTermid, "1", categoricalValueId));
		observationUnitRow.setVariables(observationUnitDataMap);

		// Create Categorical Measurement Variable with Categorical Values. One of the categorical values matches the observation unit data.
		final Map<Integer, MeasurementVariable> measurementVariableMap = new HashMap<>();
		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(variableTermid);
		measurementVariable.setLabel(variableName);
		measurementVariable.setDataTypeId(DataType.CATEGORICAL_VARIABLE.getId());
		final List<ValueReference> possibleValues = new ArrayList<>();
		possibleValues.add(new ValueReference(categoricalValueId, "ABC", "Value1"));
		measurementVariable.setPossibleValues(possibleValues);
		measurementVariableMap.put(variableTermid, measurementVariable);

		DerivedVariableUtils.extractValues(parameters, observationUnitRow, measurementVariableMap, termMissingData);

		assertEquals("ABC", parameters.get(DerivedVariableUtils.wrapTerm(String.valueOf(variableTermid))));

	}

	@Test
	public void testExtractValuesFromObservationUnitRowVariableIsCategoricalNoPossibleValuesMatched() throws ParseException {

		final String variableName = RandomStringUtils.randomAlphanumeric(10);
		final int variableTermid = RandomUtils.nextInt();

		final Map<String, Object> parameters = new HashMap<>();
		final Set<String> termMissingData = new HashSet<>();
		parameters.put(DerivedVariableUtils.wrapTerm(String.valueOf(variableTermid)), null);

		final ObservationUnitRow observationUnitRow = new ObservationUnitRow();
		final Map<String, ObservationUnitData> observationUnitDataMap = new HashMap<>();
		observationUnitDataMap.put(variableName, this.createObservationUnitDataTestData(variableTermid, "1", null));
		observationUnitRow.setVariables(observationUnitDataMap);

		// Create Categorical Measurement Variable with Categorical Values. One of the categorical values matches the observation unit data.
		final Map<Integer, MeasurementVariable> measurementVariableMap = new HashMap<>();
		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(variableTermid);
		measurementVariable.setLabel(variableName);
		measurementVariable.setDataTypeId(DataType.CATEGORICAL_VARIABLE.getId());
		final List<ValueReference> possibleValues = new ArrayList<>();
		possibleValues.add(new ValueReference(RandomUtils.nextInt(), "ABC", "Value1"));
		measurementVariable.setPossibleValues(possibleValues);
		measurementVariableMap.put(variableTermid, measurementVariable);

		DerivedVariableUtils.extractValues(parameters, observationUnitRow, measurementVariableMap, termMissingData);

		assertEquals(new BigDecimal(1), parameters.get(DerivedVariableUtils.wrapTerm(String.valueOf(variableTermid))));

	}

	@Test
	public void testExtractValuesFromObservationUnitRowVariableIsDate() throws ParseException {

		final String variableName = RandomStringUtils.randomAlphanumeric(10);
		final int variableTermid = RandomUtils.nextInt();

		final Map<String, Object> parameters = new HashMap<>();
		final Set<String> termMissingData = new HashSet<>();
		parameters.put(DerivedVariableUtils.wrapTerm(String.valueOf(variableTermid)), null);

		final ObservationUnitRow observationUnitRow = new ObservationUnitRow();
		final Map<String, ObservationUnitData> observationUnitDataMap = new HashMap<>();
		observationUnitDataMap.put(variableName, this.createObservationUnitDataTestData(variableTermid, "20200101", null));
		observationUnitRow.setVariables(observationUnitDataMap);

		// Create Categorical Measurement Variable with Categorical Values. One of the categorical values matches the observation unit data.
		final Map<Integer, MeasurementVariable> measurementVariableMap = new HashMap<>();
		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(variableTermid);
		measurementVariable.setLabel(variableName);
		measurementVariable.setDataTypeId(DataType.DATE_TIME_VARIABLE.getId());
		measurementVariableMap.put(variableTermid, measurementVariable);

		DerivedVariableUtils.extractValues(parameters, observationUnitRow, measurementVariableMap, termMissingData);

		assertEquals(DateUtil.parseDate("20200101"), parameters.get(DerivedVariableUtils.wrapTerm(String.valueOf(variableTermid))));

	}

	@Test(expected = ParseException.class)
	public void testExtractValuesFromObservationUnitRowVariableIsDateInvalid() throws ParseException {

		final String variableName = RandomStringUtils.randomAlphanumeric(10);
		final int variableTermid = RandomUtils.nextInt();

		final Map<String, Object> parameters = new HashMap<>();
		final Set<String> termMissingData = new HashSet<>();
		parameters.put(DerivedVariableUtils.wrapTerm(String.valueOf(variableTermid)), null);

		final ObservationUnitRow observationUnitRow = new ObservationUnitRow();
		final Map<String, ObservationUnitData> observationUnitDataMap = new HashMap<>();

		// Create invalid date observation unit data
		observationUnitDataMap.put(variableName, this.createObservationUnitDataTestData(variableTermid, "20200101456", null));
		observationUnitRow.setVariables(observationUnitDataMap);

		// Create Categorical Measurement Variable with Categorical Values. One of the categorical values matches the observation unit data.
		final Map<Integer, MeasurementVariable> measurementVariableMap = new HashMap<>();
		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(variableTermid);
		measurementVariable.setLabel(variableName);
		measurementVariable.setDataTypeId(DataType.DATE_TIME_VARIABLE.getId());
		measurementVariableMap.put(variableTermid, measurementVariable);

		DerivedVariableUtils.extractValues(parameters, observationUnitRow, measurementVariableMap, termMissingData);

	}

	@Test
	public void testExtractValuesFromObservationUnitRowVariableIsNumeric() throws ParseException {

		final String variableName = RandomStringUtils.randomAlphanumeric(10);
		final int variableTermid = RandomUtils.nextInt();

		final Map<String, Object> parameters = new HashMap<>();
		final Set<String> termMissingData = new HashSet<>();
		parameters.put(DerivedVariableUtils.wrapTerm(String.valueOf(variableTermid)), null);

		final ObservationUnitRow observationUnitRow = new ObservationUnitRow();
		final Map<String, ObservationUnitData> observationUnitDataMap = new HashMap<>();

		observationUnitDataMap.put(variableName, this.createObservationUnitDataTestData(variableTermid, "123", null));
		observationUnitRow.setVariables(observationUnitDataMap);

		// Create Categorical Measurement Variable with Categorical Values. One of the categorical values matches the observation unit data.
		final Map<Integer, MeasurementVariable> measurementVariableMap = new HashMap<>();
		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(variableTermid);
		measurementVariable.setLabel(variableName);
		measurementVariable.setDataTypeId(DataType.NUMERIC_VARIABLE.getId());
		measurementVariableMap.put(variableTermid, measurementVariable);

		DerivedVariableUtils.extractValues(parameters, observationUnitRow, measurementVariableMap, termMissingData);

		assertEquals(new BigDecimal(123), parameters.get(DerivedVariableUtils.wrapTerm(String.valueOf(variableTermid))));

	}

	@Test
	public void testExtractValuesFromObservationUnitRowVariableIsNumericButDataIsText() throws ParseException {

		final String variableName = RandomStringUtils.randomAlphanumeric(10);
		final int variableTermid = RandomUtils.nextInt();

		final Map<String, Object> parameters = new HashMap<>();
		final Set<String> termMissingData = new HashSet<>();
		parameters.put(DerivedVariableUtils.wrapTerm(String.valueOf(variableTermid)), null);

		final ObservationUnitRow observationUnitRow = new ObservationUnitRow();
		final Map<String, ObservationUnitData> observationUnitDataMap = new HashMap<>();

		observationUnitDataMap.put(variableName, this.createObservationUnitDataTestData(variableTermid, "ABC", null));
		observationUnitRow.setVariables(observationUnitDataMap);

		// Create Categorical Measurement Variable with Categorical Values. One of the categorical values matches the observation unit data.
		final Map<Integer, MeasurementVariable> measurementVariableMap = new HashMap<>();
		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(variableTermid);
		measurementVariable.setLabel(variableName);
		measurementVariable.setDataTypeId(DataType.NUMERIC_VARIABLE.getId());
		measurementVariableMap.put(variableTermid, measurementVariable);

		DerivedVariableUtils.extractValues(parameters, observationUnitRow, measurementVariableMap, termMissingData);

		assertEquals("ABC", parameters.get(DerivedVariableUtils.wrapTerm(String.valueOf(variableTermid))));

	}

	@Test
	public void testExtractValuesFromObservationUnitRowMissingData() throws ParseException {

		final String variableName = RandomStringUtils.randomAlphanumeric(10);
		final int variableTermid = RandomUtils.nextInt();

		final Map<String, Object> parameters = new HashMap<>();
		final Set<String> termMissingData = new HashSet<>();
		parameters.put(DerivedVariableUtils.wrapTerm(String.valueOf(variableTermid)), null);

		final ObservationUnitRow observationUnitRow = new ObservationUnitRow();
		final Map<String, ObservationUnitData> observationUnitDataMap = new HashMap<>();

		observationUnitDataMap.put(variableName, this.createObservationUnitDataTestData(variableTermid, "", null));
		observationUnitRow.setVariables(observationUnitDataMap);

		// Create Categorical Measurement Variable with Categorical Values. One of the categorical values matches the observation unit data.
		final Map<Integer, MeasurementVariable> measurementVariableMap = new HashMap<>();
		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(variableTermid);
		measurementVariable.setLabel(variableName);
		measurementVariable.setDataTypeId(DataType.NUMERIC_VARIABLE.getId());
		measurementVariableMap.put(variableTermid, measurementVariable);

		DerivedVariableUtils.extractValues(parameters, observationUnitRow, measurementVariableMap, termMissingData);

		assertEquals("", parameters.get(DerivedVariableUtils.wrapTerm(String.valueOf(variableTermid))));
		assertEquals(variableName, termMissingData.toArray()[0]);

	}

	private ObservationUnitData createObservationUnitDataTestData(final Integer VARIABLE_ID, final String value, final Integer cValueId) {
		final ObservationUnitData observationUnitData = new ObservationUnitData();
		observationUnitData.setVariableId(VARIABLE_ID);
		observationUnitData.setValue(value);
		observationUnitData.setCategoricalValueId(cValueId);
		observationUnitData.setObservationId(RandomUtils.nextInt());
		return observationUnitData;
	}

}
