package org.generationcp.commons.service.impl;

import com.google.common.collect.Lists;
import junit.framework.Assert;
import org.generationcp.commons.service.GermplasmNamingProperties;
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

public class BreedersCrossIDGeneratorTest {

	@Mock
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Mock
	private ContextUtil contextUtil;

	private static final Integer PROJECT_PREFIX_CATEGORY_ID = 3001;
	private static final String PROJECT_PREFIX_CATEGORY_VALUE = "Project_Prefix";

	private static final Integer HABITAT_DESIGNATION_CATEGORY_ID = 3002;
	private static final String HABITAT_DESIGNATION_CATEGORY_VALUE = "Habitat_Designation";

	private static final Integer SEASON_CATEGORY_ID = 10290;
	private static final String SEASON_CATEGORY_VALUE = "Dry Season";

	private BreedersCrossIDGenerator breedersCrossIDGenerator;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		GermplasmNamingProperties germplasmNamingProperties = new GermplasmNamingProperties();
		germplasmNamingProperties.setBreedersCrossIDOriginNursery("[PROJECT_PREFIX]-[HABITAT_DESIGNATION]-[SEASON]-[LOCATION]");
		germplasmNamingProperties.setBreedersCrossIDOriginTrial("[PROJECT_PREFIX]-[HABITAT_DESIGNATION]-[SEASON]-[LOCATION]");

		this.breedersCrossIDGenerator =
				new BreedersCrossIDGenerator(germplasmNamingProperties, this.ontologyVariableDataManager, this.contextUtil);

		Project testProject = new Project();
		testProject.setUniqueID("e8e4be0a-5d63-452f-8fde-b1c794ec7b1a");
		testProject.setCropType(new CropType("maize"));

		Mockito.when(this.contextUtil.getProjectInContext()).thenReturn(testProject);
		Mockito.when(this.contextUtil.getCurrentProgramUUID()).thenReturn(testProject.getUniqueID());

		Variable projectPrefixVariable = new Variable();
		Scale projectPrefixScale = new Scale();
		TermSummary projectPrefixCategory = new TermSummary(PROJECT_PREFIX_CATEGORY_ID, PROJECT_PREFIX_CATEGORY_VALUE,
				PROJECT_PREFIX_CATEGORY_VALUE);
		projectPrefixScale.addCategory(projectPrefixCategory);
		projectPrefixVariable.setScale(projectPrefixScale);
		Mockito.when(this.ontologyVariableDataManager.getVariable(Matchers.eq(testProject.getUniqueID()),
				Matchers.eq(TermId.PROJECT_PREFIX.getId()), Matchers.eq(true), Matchers.eq(false))).thenReturn(projectPrefixVariable);

		Variable habitatDesignationVariable = new Variable();
		Scale habitatDesignationScale = new Scale();
		TermSummary habitatDesignationCategory = new TermSummary(HABITAT_DESIGNATION_CATEGORY_ID, HABITAT_DESIGNATION_CATEGORY_VALUE,
				HABITAT_DESIGNATION_CATEGORY_VALUE);
		habitatDesignationScale.addCategory(habitatDesignationCategory);
		habitatDesignationVariable.setScale(habitatDesignationScale);
		Mockito.when(this.ontologyVariableDataManager.getVariable(Matchers.eq(testProject.getUniqueID()),
				Matchers.eq(TermId.HABITAT_DESIGNATION.getId()), Matchers.eq(true), Matchers.eq(false))).thenReturn(habitatDesignationVariable);

		Variable seasonVariable = new Variable();
		Scale seasonScale = new Scale();
		TermSummary seasonCategory = new TermSummary(SEASON_CATEGORY_ID, SEASON_CATEGORY_VALUE, SEASON_CATEGORY_VALUE);
		seasonScale.addCategory(seasonCategory);
		seasonVariable.setScale(seasonScale);
		Mockito.when(this.ontologyVariableDataManager.getVariable(Matchers.eq(testProject.getUniqueID()),
				Matchers.eq(TermId.SEASON_VAR.getId()), Matchers.eq(true), Matchers.eq(false))).thenReturn(seasonVariable);
	}

	@Test
	public void testGenerateBreedersCrossIDNursery() {

		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyName("StudyName");
		studyDetails.setStudyType(StudyType.N);
		workbook.setStudyDetails(studyDetails);

		MeasurementVariable locationMV = new MeasurementVariable();
		locationMV.setTermId(TermId.LOCATION_ABBR.getId());
		locationMV.setValue("IND");

		MeasurementVariable projectPrefixMV = new MeasurementVariable();
		projectPrefixMV.setTermId(TermId.PROJECT_PREFIX.getId());
		projectPrefixMV.setValue(PROJECT_PREFIX_CATEGORY_ID.toString());

		MeasurementVariable habitatDesignationMV = new MeasurementVariable();
		habitatDesignationMV.setTermId(TermId.HABITAT_DESIGNATION.getId());
		habitatDesignationMV.setValue(HABITAT_DESIGNATION_CATEGORY_ID.toString());

		MeasurementVariable seasonMV = new MeasurementVariable();
		seasonMV.setTermId(TermId.SEASON_VAR.getId());
		seasonMV.setValue(SEASON_CATEGORY_ID.toString());

		workbook.setConditions(Lists.newArrayList(locationMV, projectPrefixMV, habitatDesignationMV, seasonMV));

		String expectedBreedersCrossID = PROJECT_PREFIX_CATEGORY_VALUE + "-" + HABITAT_DESIGNATION_CATEGORY_VALUE + "-"
				+ SEASON_CATEGORY_VALUE + "-" + locationMV.getValue();

		String actualBreedersCrossID = this.breedersCrossIDGenerator.generateBreedersCrossID(workbook, null);
		Assert.assertEquals(expectedBreedersCrossID, actualBreedersCrossID);
	}

	@Test
	public void testGenerateBreedersCrossIDTrial() {

		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyName("TestStudy");
		studyDetails.setStudyType(StudyType.T);
		workbook.setStudyDetails(studyDetails);

		MeasurementVariable instance1LocationAbbrMV = new MeasurementVariable();
		instance1LocationAbbrMV.setTermId(TermId.LOCATION_ABBR.getId());
		MeasurementData instance1LocationAbbrMD = new MeasurementData();
		instance1LocationAbbrMD.setValue("IND");
		instance1LocationAbbrMD.setMeasurementVariable(instance1LocationAbbrMV);

		MeasurementVariable instance1ProjectPrefix = new MeasurementVariable();
		instance1ProjectPrefix.setTermId(TermId.PROJECT_PREFIX.getId());
		MeasurementData instance1ProjectPrefixMD = new MeasurementData();
		instance1ProjectPrefixMD.setValue(PROJECT_PREFIX_CATEGORY_VALUE);
		instance1ProjectPrefixMD.setMeasurementVariable(instance1ProjectPrefix);

		MeasurementVariable instance1HabitatDesignationMV = new MeasurementVariable();
		instance1HabitatDesignationMV.setTermId(TermId.HABITAT_DESIGNATION.getId());
		MeasurementData instance1HabitatDesignationMD = new MeasurementData();
		instance1HabitatDesignationMD.setValue(HABITAT_DESIGNATION_CATEGORY_VALUE);
		instance1HabitatDesignationMD.setMeasurementVariable(instance1HabitatDesignationMV);

		MeasurementVariable instance1SeasonMV = new MeasurementVariable();
		instance1SeasonMV.setTermId(TermId.SEASON_VAR.getId());
		MeasurementData instance1SeasonMD = new MeasurementData();
		instance1SeasonMD.setValue(SEASON_CATEGORY_VALUE);
		instance1SeasonMD.setMeasurementVariable(instance1SeasonMV);

		MeasurementVariable instance1InstanceNumberMV = new MeasurementVariable();
		instance1InstanceNumberMV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		MeasurementData instance1InstanceNumberMD = new MeasurementData();
		instance1InstanceNumberMD.setValue("1");
		instance1InstanceNumberMD.setMeasurementVariable(instance1InstanceNumberMV);

		MeasurementRow instance1Measurements = new MeasurementRow();
		instance1Measurements.setDataList(Lists.newArrayList(instance1InstanceNumberMD, instance1LocationAbbrMD,
				instance1ProjectPrefixMD, instance1HabitatDesignationMD, instance1SeasonMD));

		workbook.setTrialObservations(Lists.newArrayList(instance1Measurements));

		String expectedBreedersCrossId = PROJECT_PREFIX_CATEGORY_VALUE + "-" + HABITAT_DESIGNATION_CATEGORY_VALUE + "-"
				+ SEASON_CATEGORY_VALUE + "-" + instance1LocationAbbrMD.getValue();
		String actualBreedersCrossId = this.breedersCrossIDGenerator.generateBreedersCrossID(workbook, "1");
		Assert.assertEquals(expectedBreedersCrossId, actualBreedersCrossId);
	}
}
