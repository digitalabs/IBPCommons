package org.generationcp.commons.service.impl;

import junit.framework.Assert;
import org.generationcp.commons.service.GermplasmNamingProperties;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.junit.Test;

public class BreedersCrossIDTemplateProviderTest {

	@Test
	public void testGetKeyTemplate() {

		final GermplasmNamingProperties germplasmNamingProperties = new GermplasmNamingProperties();

		germplasmNamingProperties.setBreedersCrossIDStudy("[PROJECT_PREFIX][HABITAT_DESIGNATION]-[SEASON]-[LOCATION]");
		germplasmNamingProperties.setBreedersCrossIDStudy("[PROJECT_PREFIX][HABITAT_DESIGNATION]-[SEASON]:[LOCATION]");

		// Study
		Assert.assertEquals(germplasmNamingProperties.getBreedersCrossIDStudy(),
				new BreedersCrossIDTemplateProvider(germplasmNamingProperties, StudyTypeDto.getNurseryDto()).getKeyTemplate());

		// Trial
		Assert.assertEquals(germplasmNamingProperties.getBreedersCrossIDStudy(),
				new BreedersCrossIDTemplateProvider(germplasmNamingProperties, StudyTypeDto.getTrialDto()).getKeyTemplate());
	}
}
