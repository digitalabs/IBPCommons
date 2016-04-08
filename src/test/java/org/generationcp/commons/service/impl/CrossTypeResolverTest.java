package org.generationcp.commons.service.impl;

import junit.framework.Assert;
import org.generationcp.commons.parsing.pojo.ImportedGermplasm;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class CrossTypeResolverTest {

	@Mock
	private ContextUtil contextUtil;

	private Project testProject;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testResolveForNurseryWithSingleCrossBreedingMethod(){
		StudyType studyType = StudyType.N;
		Method breedingMethod = this.generateBreedingMethod("Single cross");
		ImportedGermplasm importedGermplasm = new ImportedGermplasm();

		testProject = new Project();
		testProject.setUniqueID("e8e4be0a-5d63-452f-8fde-b1c794ec7b1a");
		testProject.setCropType(new CropType("maize"));

		Mockito.when(this.contextUtil.getProjectInContext()).thenReturn(testProject);

		String method = new CrossTypeResolver(studyType, contextUtil, breedingMethod, importedGermplasm, null).resolve();

		Assert.assertEquals("S", method);
	}

	@Test
	public void testResolveForNurseryWithDoubleCrossBreedingMethod(){
		StudyType studyType = StudyType.N;
		Method breedingMethod = this.generateBreedingMethod("Double cross");
		ImportedGermplasm importedGermplasm = new ImportedGermplasm();

		testProject = new Project();
		testProject.setUniqueID("e8e4be0a-5d63-452f-8fde-b1c794ec7b1a");
		testProject.setCropType(new CropType("maize"));

		Mockito.when(this.contextUtil.getProjectInContext()).thenReturn(testProject);

		String method = new CrossTypeResolver(studyType, contextUtil, breedingMethod, importedGermplasm, null).resolve();

		Assert.assertEquals("D", method);
	}

	private Method generateBreedingMethod(String methodName){
		Method breedingMethod = new Method();
		breedingMethod.setMname(methodName);
		breedingMethod.setSnametype(5);
		breedingMethod.setPrefix("pre");
		breedingMethod.setSeparator("-");
		breedingMethod.setCount("[CIMCRS]");
		breedingMethod.setSuffix("suff");
		return breedingMethod;
	}

}
