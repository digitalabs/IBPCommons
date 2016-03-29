package org.generationcp.commons.service.impl;

import java.text.SimpleDateFormat;

import org.generationcp.commons.spring.util.ContextUtil;
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

import com.google.common.collect.Lists;

import junit.framework.Assert;

public class SeasonResolverTest {

	@Mock
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Mock
	private ContextUtil contextUtil;

	private static final Integer SEASON_CATEGORY_ID = 10290;
	private static final String SEASON_CATEGORY_VALUE = "Dry Season";

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
		TermSummary seasonCategory = new TermSummary(SEASON_CATEGORY_ID, SEASON_CATEGORY_VALUE, SEASON_CATEGORY_VALUE);
		seasonScale.addCategory(seasonCategory);
		seasonVariable.setScale(seasonScale);
		Mockito.when(this.ontologyVariableDataManager.getVariable(Matchers.eq(testProject.getUniqueID()),
				Matchers.eq(TermId.SEASON_VAR.getId()), Matchers.eq(true), Matchers.eq(false))).thenReturn(seasonVariable);
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

		SeasonResolver seasonResolver = new SeasonResolver(this.ontologyVariableDataManager, this.contextUtil, workbook, null);
		String season = seasonResolver.resolve();
		Assert.assertEquals("Season should be resolved to the value of Crop_season_Code variable value in Nursery settings.",
				SEASON_CATEGORY_VALUE, season);

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

		SeasonResolver seasonResolver = new SeasonResolver(this.ontologyVariableDataManager, this.contextUtil, workbook, null);
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

		SeasonResolver seasonResolver = new SeasonResolver(this.ontologyVariableDataManager, this.contextUtil, workbook, null);
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

		MeasurementVariable instance1SeasonMV = new MeasurementVariable();
		instance1SeasonMV.setTermId(TermId.SEASON_VAR.getId());
		MeasurementData instance1SeasonMD = new MeasurementData();
		instance1SeasonMD.setValue(SEASON_CATEGORY_VALUE);
		instance1SeasonMD.setMeasurementVariable(instance1SeasonMV);

		MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		MeasurementData instance1MD = new MeasurementData();
		instance1MD.setValue("1");
		instance1MD.setMeasurementVariable(instance1MV);

		MeasurementRow trialInstanceObservation = new MeasurementRow();
		trialInstanceObservation.setDataList(Lists.newArrayList(instance1MD, instance1SeasonMD));

		workbook.setTrialObservations(Lists.newArrayList(trialInstanceObservation));

		SeasonResolver seasonResolver = new SeasonResolver(this.ontologyVariableDataManager, this.contextUtil, workbook, "1");
		String season = seasonResolver.resolve();
		Assert.assertEquals("Season should be resolved to the value of Crop_season_Code variable value in environment level settings.",
				SEASON_CATEGORY_VALUE, season);
	}

	@Test
	public void testResolveForTrialWithSeasonVariableButNoValue() {
		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.T);
		workbook.setStudyDetails(studyDetails);

		MeasurementVariable instance1SeasonMV = new MeasurementVariable();
		instance1SeasonMV.setTermId(TermId.SEASON_VAR.getId());
		MeasurementData instance1SeasonMD = new MeasurementData();
		// Variable present but has no value
		instance1SeasonMD.setMeasurementVariable(instance1SeasonMV);

		MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		MeasurementData instance1MD = new MeasurementData();
		instance1MD.setValue("1");
		instance1MD.setMeasurementVariable(instance1MV);

		MeasurementRow trialInstanceObservation = new MeasurementRow();
		trialInstanceObservation.setDataList(Lists.newArrayList(instance1MD, instance1SeasonMD));

		workbook.setTrialObservations(Lists.newArrayList(trialInstanceObservation));

		SeasonResolver seasonResolver = new SeasonResolver(this.ontologyVariableDataManager, this.contextUtil, workbook, "1");
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

		SeasonResolver seasonResolver = new SeasonResolver(this.ontologyVariableDataManager, this.contextUtil, workbook, "1");
		String season = seasonResolver.resolve();
		Assert.assertEquals(
				"Season should be defaulted to current year and month when Crop_season_Code variable is not present in environment level settings.",
				currentYearAndMonth, season);
	}
}
