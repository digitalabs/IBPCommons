package org.generationcp.commons.service.impl;

import junit.framework.Assert;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.pojos.Method;
import org.junit.Test;

public class CrossTypeResolverTest {

	@Test
	public void testResolveForNurseryWithSingleCrossBreedingMethod(){
		StudyType studyType = StudyType.N;
		Method breedingMethod = this.generateBreedingMethod("Single cross");

		String method = new CrossTypeResolver(studyType, breedingMethod, null).resolve();

		Assert.assertEquals("S", method);
	}

	@Test
	public void testResolveForNurseryWithDoubleCrossBreedingMethod(){
		StudyType studyType = StudyType.N;
		Method breedingMethod = this.generateBreedingMethod("Double cross");

		String method = new CrossTypeResolver(studyType, breedingMethod, null).resolve();

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
