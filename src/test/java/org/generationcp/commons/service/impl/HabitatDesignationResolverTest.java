package org.generationcp.commons.service.impl;

import com.google.common.collect.Lists;
import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

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

public class HabitatDesignationResolverTest {

	@Mock
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Mock
	private ContextUtil contextUtil;

	private static final Integer HABITAT_CATEGORY_ID = 3002;
	private static final String HABITAT_CATEGORY_VALUE = "Habitat_Designation";

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
		TermSummary categories = new TermSummary(HABITAT_CATEGORY_ID, HABITAT_CATEGORY_VALUE, HABITAT_CATEGORY_VALUE);
		seasonScale.addCategory(categories);
		variable.setScale(seasonScale);
		Mockito.when(this.ontologyVariableDataManager.getVariable(Matchers.eq(testProject.getUniqueID()),
				Matchers.eq(TermId.HABITAT_DESIGNATION.getId()), Matchers.eq(true), Matchers.eq(false))).thenReturn(variable);
	}

	@Test
	public void testResolveForNurseryWithHabitatVariableAndValue() {

		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.N);
		workbook.setStudyDetails(studyDetails);

		MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(TermId.HABITAT_DESIGNATION.getId());
		measurementVariable.setValue(HABITAT_CATEGORY_ID.toString());

		workbook.setConditions(Lists.newArrayList(measurementVariable));

		MeasurementRow trailInstanceObservation = workbook.getTrialObservationByTrialInstanceNo(TermId.TRIAL_INSTANCE_FACTOR.getId());
		StudyType studyType = workbook.getStudyDetails().getStudyType();

		HabitatDesignationResolver
				habitatDesignationResolver = new HabitatDesignationResolver(this.ontologyVariableDataManager, this.contextUtil, workbook.getConditions(),
				trailInstanceObservation, studyType);
		String designation = habitatDesignationResolver.resolve();
		Assert.assertEquals("Habitat Designation should be resolved to the value of Habitat_Designation variable value in Nursery settings.",
				HABITAT_CATEGORY_VALUE, designation);

	}

	@Test
	public void testResolveForTrialWithHabitatVariableAndValue() {
		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.T);
		workbook.setStudyDetails(studyDetails);

		MeasurementVariable instance1HabitatMV = new MeasurementVariable();
		instance1HabitatMV.setTermId(TermId.HABITAT_DESIGNATION.getId());
		MeasurementData instance1Habitat = new MeasurementData();
		instance1Habitat.setValue(HABITAT_CATEGORY_VALUE);
		instance1Habitat.setMeasurementVariable(instance1HabitatMV);

		MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		MeasurementData instance1MD = new MeasurementData();
		instance1MD.setValue("1");
		instance1MD.setMeasurementVariable(instance1MV);

		MeasurementRow trialInstanceObservation = new MeasurementRow();
		trialInstanceObservation.setDataList(Lists.newArrayList(instance1MD, instance1Habitat));

		workbook.setTrialObservations(Lists.newArrayList(trialInstanceObservation));
		
		StudyType studyType = workbook.getStudyDetails().getStudyType();

		HabitatDesignationResolver
				habitatDesignationResolver = new HabitatDesignationResolver(this.ontologyVariableDataManager, this.contextUtil, workbook.getConditions(),
				trialInstanceObservation, studyType);
		String season = habitatDesignationResolver.resolve();
		Assert.assertEquals("Habitat Designation should be resolved to the value of Habitat_Designation variable value in environment level settings.",
				HABITAT_CATEGORY_VALUE, season);
	}
	
	@Test
	public void testResolveForTrialWithHabitatConditions() {
		Workbook workbook = new Workbook();
		StudyDetails studyDetails = new StudyDetails();
		studyDetails.setStudyType(StudyType.T);
		workbook.setStudyDetails(studyDetails);

		MeasurementVariable instance1HabitatMV = new MeasurementVariable();
		instance1HabitatMV.setTermId(TermId.HABITAT_DESIGNATION.getId());
		instance1HabitatMV.setValue(HABITAT_CATEGORY_VALUE);

		MeasurementVariable instance1MV = new MeasurementVariable();
		instance1MV.setTermId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		instance1MV.setValue("1");
		
		MeasurementRow trialInstanceObservation = null;

		List<MeasurementVariable> conditions = new ArrayList<MeasurementVariable>();
		conditions.add(instance1MV);
		conditions.add(instance1HabitatMV);
		workbook.setConditions(conditions );

		StudyType studyType = workbook.getStudyDetails().getStudyType();

		HabitatDesignationResolver
				habitatDesignationResolver = new HabitatDesignationResolver(this.ontologyVariableDataManager, this.contextUtil, workbook.getConditions(),
				trialInstanceObservation, studyType);
		String season = habitatDesignationResolver.resolve();
		Assert.assertEquals("Habitat Designation should be resolved to the value of Habitat_Designation variable value in environment level settings.",
				HABITAT_CATEGORY_VALUE, season);
	}
}
