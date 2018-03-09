package org.generationcp.commons.service.impl;

import junit.framework.Assert;
import org.generationcp.commons.service.GermplasmNamingProperties;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.junit.Test;

public class BreedersCrossIDTemplateProviderTest {

	@Test
	public void testGetKeyTemplate() {

		final GermplasmNamingProperties germplasmNamingProperties = new GermplasmNamingProperties();

		germplasmNamingProperties.setBreedersCrossIDNursery("[PROJECT_PREFIX][HABITAT_DESIGNATION]-[SEASON]-[LOCATION]");
		germplasmNamingProperties.setBreedersCrossIDTrial("[PROJECT_PREFIX][HABITAT_DESIGNATION]-[SEASON]:[LOCATION]");

		// Nursery
		Assert.assertEquals(germplasmNamingProperties.getBreedersCrossIDNursery(),
				new BreedersCrossIDTemplateProvider(germplasmNamingProperties, new StudyTypeDto("N")).getKeyTemplate());

		// Trial
		Assert.assertEquals(germplasmNamingProperties.getBreedersCrossIDTrial(),
				new BreedersCrossIDTemplateProvider(germplasmNamingProperties, new StudyTypeDto("T")).getKeyTemplate());
	}
}
