package org.generationcp.commons.service.impl;

import com.google.common.collect.Lists;
import junit.framework.Assert;
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

public class ProjectPrefixResolverTest {

	@Mock
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Mock
	private ContextUtil contextUtil;

	private static final Integer PROJECT_CATEGORY_ID = 3001;
	private static final String PROJECT_CATEGORY_VALUE = "Project_Prefix";

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		Project testProject = new Project();
		testProject.setUniqueID("e8e4be0a-5d63-452f-8fde-b1c794ec7b1a");
		testProject.setCropType(new CropType("maize"));
		Mockito.when(this.contextUtil.getProjectInContext()).thenReturn(testProject);
		Mockito.when(this.contextUtil.getCurrentProgramUUID()).thenReturn(testProject.getUniqueID());

		Variable variable = new Variable();
		Scale seasonScale = new Scale();
		TermSummary categories = new TermSummary(PROJECT_CATEGORY_ID, PROJECT_CATEGORY_VALUE, PROJECT_CATEGORY_VALUE);
		seasonScale.addCategory(categories);
		variable.setScale(seasonScale);
		Mockito.when(this.ontologyVariableDataManager.getVariable(Matchers.eq(testProject.getUniqueID()),
				Matchers.eq(TermId.PROJECT_PREFIX.getId()), Matchers.eq(true), Matchers.eq(false))).thenReturn(variable);
	}

	@Test
	public void testResolveForNurseryWithProgramVariableAndValue() {

		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.N);
		workbook.setStudyDetails(studyDetails);

		MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(TermId.PROJECT_PREFIX.getId());
		measurementVariable.setValue(PROJECT_CATEGORY_ID.toString());

		workbook.setConditions(Lists.newArrayList(measurementVariable));

		ProjectPrefixResolver
				projectPrefixResolver = new ProjectPrefixResolver(this.ontologyVariableDataManager, this.contextUtil, workbook, null);
		String program = projectPrefixResolver.resolve();
		Assert.assertEquals("Program should be resolved to the value of Project_Prefix variable value in Nursery settings.",
				PROJECT_CATEGORY_VALUE, program);

	}

	@Test
	public void testResolveForTrialWithProgramVariableAndValue() {
		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.T);
		workbook.setStudyDetails(studyDetails);

		MeasurementVariable instance1ProgramMV = new MeasurementVariable();
		instance1ProgramMV.setTermId(TermId.PROJECT_PREFIX.getId());
		MeasurementData instance1ProgramMD = new MeasurementData();
		instance1ProgramMD.setValue(PROJECT_CATEGORY_VALUE);
		instance1ProgramMD.setMeasurementVariable(instance1ProgramMV);

		MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		MeasurementData instance1MD = new MeasurementData();
		instance1MD.setValue("1");
		instance1MD.setMeasurementVariable(instance1MV);

		MeasurementRow trialInstanceObservation = new MeasurementRow();
		trialInstanceObservation.setDataList(Lists.newArrayList(instance1MD, instance1ProgramMD));

		workbook.setTrialObservations(Lists.newArrayList(trialInstanceObservation));

		ProjectPrefixResolver
				projectPrefixResolver = new ProjectPrefixResolver(this.ontologyVariableDataManager, this.contextUtil, workbook, "1");
		String season = projectPrefixResolver.resolve();
		Assert.assertEquals("Program should be resolved to the value of Project_Prefix variable value in environment level settings.",
				PROJECT_CATEGORY_VALUE, season);
	}
}
