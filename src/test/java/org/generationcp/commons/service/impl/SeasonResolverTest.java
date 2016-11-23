package org.generationcp.commons.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import junit.framework.Assert;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.data.initializer.ValueReferenceTestDataInitializer;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class SeasonResolverTest {


	private ValueReferenceTestDataInitializer valueReferenceTestDataInitializer;

	@Mock
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Mock
	private ContextUtil contextUtil;

	private static final Integer SEASON_CATEGORY_ID = 10290;
	private static final String SEASON_CATEGORY_NAME_VALUE = "1";
	private static final String SEASON_CATEGORY_DESCRIPTION_VALUE = "Dry Season";
	public static final String DESCRIPTION_STRING_NOT_FOUND_IN_POSSIBLE_VALUES = "Description not found in possible values";

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		Project testProject = new Project();
		testProject.setUniqueID("e8e4be0a-5d63-452f-8fde-b1c794ec7b1a");
		testProject.setCropType(new CropType("maize"));
		Mockito.when(this.contextUtil.getProjectInContext()).thenReturn(testProject);
		Mockito.when(this.contextUtil.getCurrentProgramUUID()).thenReturn(testProject.getUniqueID());

		Variable seasonVariable = new Variable();
		Scale seasonScale = new Scale();
		TermSummary seasonCategory = new TermSummary(SEASON_CATEGORY_ID, SEASON_CATEGORY_NAME_VALUE, SEASON_CATEGORY_DESCRIPTION_VALUE);
		seasonScale.addCategory(seasonCategory);
		seasonVariable.setScale(seasonScale);
		Mockito.when(this.ontologyVariableDataManager.getVariable(Matchers.eq(testProject.getUniqueID()),
				Matchers.eq(TermId.SEASON_VAR.getId()), Matchers.eq(true), Matchers.eq(false))).thenReturn(seasonVariable);

		this.valueReferenceTestDataInitializer = new ValueReferenceTestDataInitializer();
	}

	@Test
	public void testResolveForNurseryWithSeasonVariableAndValue() {

		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.N);
		workbook.setStudyDetails(studyDetails);

		MeasurementVariable seasonMV = new MeasurementVariable();
		seasonMV.setTermId(TermId.SEASON_VAR.getId());
		seasonMV.setValue(SEASON_CATEGORY_ID.toString());

		workbook.setConditions(Lists.newArrayList(seasonMV));

		MeasurementRow trailInstanceObservation = workbook.getTrialObservationByTrialInstanceNo(TermId.TRIAL_INSTANCE_FACTOR.getId());
		StudyType studyType = workbook.getStudyDetails().getStudyType();

		SeasonResolver seasonResolver = new SeasonResolver(this.ontologyVariableDataManager, this.contextUtil, workbook.getConditions(),
				trailInstanceObservation, studyType);
		String season = seasonResolver.resolve();
		Assert.assertEquals("Season should be resolved to the value of Crop_season_Code variable value in Nursery settings.",
				SEASON_CATEGORY_NAME_VALUE, season);

	}

	@Test
	public void testResolveForNurseryWithSeasonVariableButNoValue() {

		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.N);
		workbook.setStudyDetails(studyDetails);

		MeasurementVariable seasonMV = new MeasurementVariable();
		seasonMV.setTermId(TermId.SEASON_VAR.getId());
		// Variable presnet but no value

		workbook.setConditions(Lists.newArrayList(seasonMV));

		MeasurementRow trailInstanceObservation = workbook.getTrialObservationByTrialInstanceNo(TermId.TRIAL_INSTANCE_FACTOR.getId());
		StudyType studyType = workbook.getStudyDetails().getStudyType();

		SeasonResolver seasonResolver = new SeasonResolver(this.ontologyVariableDataManager, this.contextUtil, workbook.getConditions(),
				trailInstanceObservation, studyType);
		String season = seasonResolver.resolve();

		SimpleDateFormat formatter = new SimpleDateFormat("YYYYMM");
		String currentYearAndMonth = formatter.format(new java.util.Date());

		Assert.assertEquals(
				"Season should be defaulted to current year and month when Crop_season_Code variable is present but has no value.",
				currentYearAndMonth, season);
	}

	@Test
	public void testResolveForNurseryWithoutSeasonVariable() {

		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.N);
		workbook.setStudyDetails(studyDetails);

		MeasurementRow trailInstanceObservation = workbook.getTrialObservationByTrialInstanceNo(TermId.TRIAL_INSTANCE_FACTOR.getId());
		StudyType studyType = workbook.getStudyDetails().getStudyType();

		SeasonResolver seasonResolver = new SeasonResolver(this.ontologyVariableDataManager, this.contextUtil, workbook.getConditions(),
				trailInstanceObservation, studyType);
		String season = seasonResolver.resolve();

		SimpleDateFormat formatter = new SimpleDateFormat("YYYYMM");
		String currentYearAndMonth = formatter.format(new java.util.Date());

		Assert.assertEquals("Season should be defaulted to current year and month when Crop_season_Code variable is not present.",
				currentYearAndMonth,
				season);
	}

	@Test
	public void testResolveForTrialWithSeasonVariableAndValue() {
		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.T);
		workbook.setStudyDetails(studyDetails);

		MeasurementVariable firstInstanceSeasonMeasurementVariable = new MeasurementVariable();
		firstInstanceSeasonMeasurementVariable.setTermId(TermId.SEASON_VAR.getId());
		firstInstanceSeasonMeasurementVariable.setPossibleValues(this.createTestPossibleValuesForSeasonVariable());
		MeasurementData instance1SeasonMD = new MeasurementData();
		instance1SeasonMD.setValue(SEASON_CATEGORY_DESCRIPTION_VALUE);
		instance1SeasonMD.setMeasurementVariable(firstInstanceSeasonMeasurementVariable);

		MeasurementVariable firstInstanceMeasurementVariable = new MeasurementVariable();
		firstInstanceMeasurementVariable.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		MeasurementData firstInstanceMeasurementData = new MeasurementData();
		firstInstanceMeasurementData.setValue("1");
		firstInstanceMeasurementData.setMeasurementVariable(firstInstanceMeasurementVariable);

		MeasurementRow trialInstanceObservation = new MeasurementRow();
		trialInstanceObservation.setDataList(Lists.newArrayList(firstInstanceMeasurementData, instance1SeasonMD));

		workbook.setTrialObservations(Lists.newArrayList(trialInstanceObservation));

		StudyType studyType = workbook.getStudyDetails().getStudyType();

		SeasonResolver seasonResolver = new SeasonResolver(this.ontologyVariableDataManager, this.contextUtil, workbook.getConditions(),
				trialInstanceObservation, studyType);
		String season = seasonResolver.resolve();
		Assert.assertEquals("Season should be resolved to the value of Crop_season_Code variable value in environment level settings.",
				SEASON_CATEGORY_NAME_VALUE, season);
	}

	@Test
	public void testResolveForTrialWithSeasonVariableButNoValue() {
		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.T);
		workbook.setStudyDetails(studyDetails);

		MeasurementVariable firstInstanceSeasonMeasurementVariable = new MeasurementVariable();
		firstInstanceSeasonMeasurementVariable.setTermId(TermId.SEASON_VAR.getId());
		MeasurementData instance1SeasonMD = new MeasurementData();
		// Variable present but has no value
		instance1SeasonMD.setMeasurementVariable(firstInstanceSeasonMeasurementVariable);

		MeasurementVariable firstInstanceMeasurementVariable = new MeasurementVariable();
		firstInstanceMeasurementVariable.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		MeasurementData firstInstanceMeasurementData = new MeasurementData();
		firstInstanceMeasurementData.setValue("1");
		firstInstanceMeasurementData.setMeasurementVariable(firstInstanceMeasurementVariable);

		MeasurementRow trialInstanceObservation = new MeasurementRow();
		trialInstanceObservation.setDataList(Lists.newArrayList(firstInstanceMeasurementData, instance1SeasonMD));

		workbook.setTrialObservations(Lists.newArrayList(trialInstanceObservation));

		StudyType studyType = workbook.getStudyDetails().getStudyType();

		SeasonResolver seasonResolver = new SeasonResolver(this.ontologyVariableDataManager, this.contextUtil, workbook.getConditions(),
				trialInstanceObservation, studyType);
		String season = seasonResolver.resolve();

		SimpleDateFormat formatter = new SimpleDateFormat("YYYYMM");
		String currentYearAndMonth = formatter.format(new java.util.Date());

		Assert.assertEquals(
				"Season should be defaulted to current year and month when Crop_season_Code variable in environment level settings, is present but has no value.",
				currentYearAndMonth, season);
	}

	@Test
	public void testResolveForTrialWithoutSeasonVariable() {
		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.T);
		workbook.setStudyDetails(studyDetails);

		SimpleDateFormat formatter = new SimpleDateFormat("YYYYMM");
		String currentYearAndMonth = formatter.format(new java.util.Date());

		MeasurementRow trailInstanceObservation = workbook.getTrialObservationByTrialInstanceNo(TermId.TRIAL_INSTANCE_FACTOR.getId());
		StudyType studyType = workbook.getStudyDetails().getStudyType();

		SeasonResolver seasonResolver = new SeasonResolver(this.ontologyVariableDataManager, this.contextUtil, workbook.getConditions(),
				trailInstanceObservation, studyType);
		String season = seasonResolver.resolve();
		Assert.assertEquals(
				"Season should be defaulted to current year and month when Crop_season_Code variable is not present in environment level settings.",
				currentYearAndMonth, season);
	}

	@Test
	public void testGetValueFromTrialInstanceMeasurementDataSeasonDesscriptionIsPresentInPossibleValues() {

		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.T);
		workbook.setStudyDetails(studyDetails);

		MeasurementRow trailInstanceObservation = workbook.getTrialObservationByTrialInstanceNo(TermId.TRIAL_INSTANCE_FACTOR.getId());
		StudyType studyType = workbook.getStudyDetails().getStudyType();

		SeasonResolver seasonResolver = new SeasonResolver(this.ontologyVariableDataManager, this.contextUtil, workbook.getConditions(),
				trailInstanceObservation, studyType);

		MeasurementVariable firstInstanceSeasonMeasurementVariable = new MeasurementVariable();
		firstInstanceSeasonMeasurementVariable.setTermId(TermId.SEASON_VAR.getId());
		firstInstanceSeasonMeasurementVariable.setPossibleValues(this.createTestPossibleValuesForSeasonVariable());
		MeasurementData firstInstanceSeasonMeasurementData = new MeasurementData();
		firstInstanceSeasonMeasurementData.setValue(SEASON_CATEGORY_DESCRIPTION_VALUE);
		firstInstanceSeasonMeasurementData.setMeasurementVariable(firstInstanceSeasonMeasurementVariable);

		Assert.assertEquals("The method should return the Season Name, not the Season Description.", SEASON_CATEGORY_NAME_VALUE, seasonResolver.getValueFromTrialInstanceMeasurementData(firstInstanceSeasonMeasurementData));

	}

	@Test
	public void testGetValueFromTrialInstanceMeasurementDataSeasonDesscriptionDoesNotExistInPossibleValues() {

		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.T);
		workbook.setStudyDetails(studyDetails);

		MeasurementRow trailInstanceObservation = workbook.getTrialObservationByTrialInstanceNo(TermId.TRIAL_INSTANCE_FACTOR.getId());
		StudyType studyType = workbook.getStudyDetails().getStudyType();

		SeasonResolver seasonResolver = new SeasonResolver(this.ontologyVariableDataManager, this.contextUtil, workbook.getConditions(),
				trailInstanceObservation, studyType);

		MeasurementVariable firstInstanceSeasonMeasurementVariable = new MeasurementVariable();
		firstInstanceSeasonMeasurementVariable.setTermId(TermId.SEASON_VAR.getId());
		firstInstanceSeasonMeasurementVariable.setPossibleValues(this.createTestPossibleValuesForSeasonVariable());
		MeasurementData firstInstanceSeasonMeasurementData = new MeasurementData();
		firstInstanceSeasonMeasurementData.setValue(DESCRIPTION_STRING_NOT_FOUND_IN_POSSIBLE_VALUES);
		firstInstanceSeasonMeasurementData.setMeasurementVariable(firstInstanceSeasonMeasurementVariable);

		Assert.assertEquals("The method should return the Season Measurement Data value as it is since the value is not found in possible values.",
				DESCRIPTION_STRING_NOT_FOUND_IN_POSSIBLE_VALUES, seasonResolver.getValueFromTrialInstanceMeasurementData(firstInstanceSeasonMeasurementData));

	}

	@Test
	public void testFindValueReferenceByDescriptionPossibleValues() {

		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.T);
		workbook.setStudyDetails(studyDetails);

		MeasurementRow trailInstanceObservation = workbook.getTrialObservationByTrialInstanceNo(TermId.TRIAL_INSTANCE_FACTOR.getId());
		StudyType studyType = workbook.getStudyDetails().getStudyType();

		SeasonResolver seasonResolver = new SeasonResolver(this.ontologyVariableDataManager, this.contextUtil, workbook.getConditions(),
				trailInstanceObservation, studyType);

		Optional<ValueReference> result1 = seasonResolver.findValueReferenceByDescription(SEASON_CATEGORY_DESCRIPTION_VALUE, null);
		Assert.assertFalse(result1.isPresent());

		Optional<ValueReference> result2 = seasonResolver.findValueReferenceByDescription(SEASON_CATEGORY_DESCRIPTION_VALUE, this.createTestPossibleValuesForSeasonVariable());
		Assert.assertTrue(result2.isPresent());

		Optional<ValueReference> result3 = seasonResolver.findValueReferenceByDescription(DESCRIPTION_STRING_NOT_FOUND_IN_POSSIBLE_VALUES, this.createTestPossibleValuesForSeasonVariable());
		Assert.assertFalse(result3.isPresent());


	}

	@Test
	public void testResolveForTrialWithSeasonVariableConditions() {
		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.T);
		workbook.setStudyDetails(studyDetails);

		MeasurementVariable firstInstanceSeasonMeasurementVariable = new MeasurementVariable();
		firstInstanceSeasonMeasurementVariable.setTermId(TermId.SEASON_VAR.getId());
		firstInstanceSeasonMeasurementVariable.setPossibleValues(this.createTestPossibleValuesForSeasonVariable());
		firstInstanceSeasonMeasurementVariable.setValue(SEASON_CATEGORY_DESCRIPTION_VALUE);

		MeasurementVariable firstInstanceMeasurementVariable = new MeasurementVariable();
		firstInstanceMeasurementVariable.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		firstInstanceMeasurementVariable.setValue("1");
		
		MeasurementRow trialInstanceObservation = null;
				
		List<MeasurementVariable> conditions = new ArrayList<MeasurementVariable>();
		conditions.add(firstInstanceSeasonMeasurementVariable);
		conditions.add(firstInstanceMeasurementVariable);
		workbook.setConditions(conditions);

		StudyType studyType = workbook.getStudyDetails().getStudyType();

		SeasonResolver seasonResolver = new SeasonResolver(this.ontologyVariableDataManager, this.contextUtil, workbook.getConditions(),
				trialInstanceObservation, studyType);
		String season = seasonResolver.resolve();
		Assert.assertEquals("Season should be resolved to the value of Crop_season_Code variable value in environment level settings.",
				SEASON_CATEGORY_NAME_VALUE, season);
	}
	
	private List<ValueReference> createTestPossibleValuesForSeasonVariable() {
		final List<ValueReference> possibleValues = new ArrayList<>();
		possibleValues.add(this.valueReferenceTestDataInitializer.createValueReference(SEASON_CATEGORY_ID, SEASON_CATEGORY_NAME_VALUE, SEASON_CATEGORY_DESCRIPTION_VALUE));
		return possibleValues;
	}
}
