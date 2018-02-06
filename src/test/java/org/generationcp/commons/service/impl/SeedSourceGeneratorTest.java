
package org.generationcp.commons.service.impl;

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

import com.google.common.collect.Lists;

import junit.framework.Assert;

/**
 * This is more of an integration test (not a pure unit test) of all key code generation pieces for the seed source use case.
 */
public class SeedSourceGeneratorTest {

	@Mock
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Mock
	private ContextUtil contextUtil;

	private static final Integer SEASON_CATEGORY_ID = 10290;
	private static final String SEASON_CATEGORY_VALUE = "Dry Season";

	private SeedSourceGenerator seedSourceGenerator;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		GermplasmNamingProperties germplasmNamingProperties = new GermplasmNamingProperties();
		germplasmNamingProperties.setGermplasmOriginNurseriesDefault("[NAME]:[PLOTNO]:[PLANT_NO]");
		germplasmNamingProperties.setGermplasmOriginNurseriesMaize("[LOCATION][SEASON]-[NAME]-[PLOTNO][SELECTION_NUMBER]");
		germplasmNamingProperties.setGermplasmOriginNurseriesWheat("[LOCATION]\\[SEASON]\\[NAME]\\[PLOTNO]");
		germplasmNamingProperties.setGermplasmOriginTrialsDefault("[NAME]:[LOCATION]:[SEASON]:[PLOTNO]:[PLANT_NO]");
		germplasmNamingProperties.setGermplasmOriginTrialsMaize("[LOCATION][SEASON]-[NAME]-[PLOTNO][SELECTION_NUMBER]");
		germplasmNamingProperties.setGermplasmOriginTrialsWheat("[LOCATION]\\[SEASON]\\[NAME]\\[PLOTNO]");

		this.seedSourceGenerator =
				new SeedSourceGenerator(germplasmNamingProperties, this.ontologyVariableDataManager, this.contextUtil);
	}

	private void setCurrentCrop(String crop) {
		Project testProject = new Project();
		testProject.setUniqueID("e8e4be0a-5d63-452f-8fde-b1c794ec7b1a");
		testProject.setCropType(new CropType(crop));

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
	public void testGenerateSeedSourceNursery() {

		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyName("StudyName");
		studyDetails.setStudyType(StudyType.N);
		workbook.setStudyDetails(studyDetails);

		MeasurementVariable locationMV = new MeasurementVariable();
		locationMV.setTermId(TermId.LOCATION_ABBR.getId());
		locationMV.setValue("IND");

		MeasurementVariable seasonMV = new MeasurementVariable();
		seasonMV.setTermId(TermId.SEASON_VAR.getId());
		seasonMV.setValue(SEASON_CATEGORY_ID.toString());

		workbook.setConditions(Lists.newArrayList(locationMV, seasonMV));

		setCurrentCrop("rice");
		String seedSource = this.seedSourceGenerator.generateSeedSource(workbook, null, "2", "3", studyDetails.getStudyName(), null);
		Assert.assertEquals("StudyName:3:", seedSource);
		// with plant number
		seedSource = this.seedSourceGenerator.generateSeedSource(workbook, null, "2", "3", studyDetails.getStudyName(), "1");
		Assert.assertEquals("StudyName:3:1", seedSource);

		setCurrentCrop("wheat");
		seedSource = this.seedSourceGenerator.generateSeedSource(workbook, null, "2", "3", studyDetails.getStudyName(), null);
		Assert.assertEquals("IND\\Dry Season\\StudyName\\3", seedSource);

		setCurrentCrop("maize");
		// with selection number
		seedSource = this.seedSourceGenerator.generateSeedSource(workbook, null, "2", "3", studyDetails.getStudyName(), null);
		Assert.assertEquals("INDDry Season-StudyName-3-2", seedSource);

		// without selection number
		seedSource = this.seedSourceGenerator.generateSeedSource(workbook, null, null, "3", studyDetails.getStudyName(), null);
		Assert.assertEquals("INDDry Season-StudyName-3", seedSource);
	}

	@Test
	public void testGenerateSeedSourceTrial() {

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
		instance1Measurements.setDataList(Lists.newArrayList(instance1InstanceNumberMD, instance1LocationAbbrMD, instance1SeasonMD));

		workbook.setTrialObservations(Lists.newArrayList(instance1Measurements));

		setCurrentCrop("rice");
		String seedSource = this.seedSourceGenerator.generateSeedSource(workbook, "1", "2", "3", studyDetails.getStudyName(), null);
		Assert.assertEquals("TestStudy:IND:Dry Season:3:", seedSource);
		
		// with Plant Number 
		seedSource = this.seedSourceGenerator.generateSeedSource(workbook, "1", "2", "3", studyDetails.getStudyName(), "4");
		Assert.assertEquals("TestStudy:IND:Dry Season:3:4", seedSource);

		setCurrentCrop("wheat");
		seedSource = this.seedSourceGenerator.generateSeedSource(workbook, "1", "2", "3", studyDetails.getStudyName(), null);
		Assert.assertEquals("IND\\Dry Season\\TestStudy\\3", seedSource);

		setCurrentCrop("maize");
		// with selection number
		seedSource = this.seedSourceGenerator.generateSeedSource(workbook, "1", "2", "3", studyDetails.getStudyName(), null);
		Assert.assertEquals("INDDry Season-TestStudy-3-2", seedSource);
		// without selection number
		seedSource = this.seedSourceGenerator.generateSeedSource(workbook, "1", null, "3", studyDetails.getStudyName(), null);
		Assert.assertEquals("INDDry Season-TestStudy-3", seedSource);
	}

	@Test
	public void testGenerateSeedSourceForCrosses() {
		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyName("StudyName");
		studyDetails.setStudyType(StudyType.N);
		workbook.setStudyDetails(studyDetails);

		MeasurementVariable locationMV = new MeasurementVariable();
		locationMV.setTermId(TermId.LOCATION_ABBR.getId());
		locationMV.setValue("IND");

		MeasurementVariable seasonMV = new MeasurementVariable();
		seasonMV.setTermId(TermId.SEASON_VAR.getId());
		seasonMV.setValue(SEASON_CATEGORY_ID.toString());

		workbook.setConditions(Lists.newArrayList(locationMV, seasonMV));

		setCurrentCrop("maize");
		String crossSeedSource =
				this.seedSourceGenerator.generateSeedSourceForCross(workbook, "1", "2", "MaleStudyName", "FemaleStudyName");
		Assert.assertEquals("INDDry Season-FemaleStudyName-2/INDDry Season-MaleStudyName-1", crossSeedSource);
		
		setCurrentCrop("rice");
		crossSeedSource =
				this.seedSourceGenerator.generateSeedSourceForCross(workbook, "1", "2", "MaleStudyName", "FemaleStudyName");
		Assert.assertEquals("FemaleStudyName:2:/MaleStudyName:1:", crossSeedSource);
	}

}
