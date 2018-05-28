package org.generationcp.commons.service.impl;

import junit.framework.Assert;
import org.generationcp.commons.service.GermplasmNamingProperties;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.junit.Test;

public class SeedSourceTemplateProviderTest {

	@Test
	public void testGetKeyTemplate() {

		final GermplasmNamingProperties germplasmNamingProperties = new GermplasmNamingProperties();
		germplasmNamingProperties.setGermplasmOriginStudiesDefault("[NAME]:[PLOTNO]");
		germplasmNamingProperties.setGermplasmOriginStudiesMaize("[LOCATION][SEASON]-[NAME]-[PLOTNO][SELECTION_NUMBER]");
		germplasmNamingProperties.setGermplasmOriginStudiesWheat("[LOCATION]\\[SEASON]\\[NAME]\\[PLOTNO]");

		germplasmNamingProperties.setGermplasmOriginStudiesDefault("[NAME]:[LOCATION]:[SEASON]:[PLOTNO]");
		germplasmNamingProperties.setGermplasmOriginStudiesMaize("[LOCATION]\\[SEASON]\\[NAME]\\[PLOTNO]");
		germplasmNamingProperties.setGermplasmOriginStudiesWheat("[LOCATION][SEASON]-[NAME]-[PLOTNO]");

		// Studies
		Assert.assertEquals(germplasmNamingProperties.getGermplasmOriginStudiesDefault(),
				new SeedSourceTemplateProvider(germplasmNamingProperties, new StudyTypeDto(StudyTypeDto.NURSERY_NAME), "rice").getKeyTemplate());

		Assert.assertEquals(germplasmNamingProperties.getGermplasmOriginStudiesMaize(),
				new SeedSourceTemplateProvider(germplasmNamingProperties, new StudyTypeDto(StudyTypeDto.NURSERY_NAME), "maize").getKeyTemplate());

		Assert.assertEquals(germplasmNamingProperties.getGermplasmOriginStudiesWheat(),
				new SeedSourceTemplateProvider(germplasmNamingProperties, new StudyTypeDto(StudyTypeDto.NURSERY_NAME), "wheat").getKeyTemplate());

		// Trials
		Assert.assertEquals(germplasmNamingProperties.getGermplasmOriginStudiesDefault(),
				new SeedSourceTemplateProvider(germplasmNamingProperties, new StudyTypeDto(StudyTypeDto.TRIAL_NAME), "rice").getKeyTemplate());

		Assert.assertEquals(germplasmNamingProperties.getGermplasmOriginStudiesMaize(),
				new SeedSourceTemplateProvider(germplasmNamingProperties, new StudyTypeDto(StudyTypeDto.TRIAL_NAME), "maize").getKeyTemplate());

		Assert.assertEquals(germplasmNamingProperties.getGermplasmOriginStudiesWheat(),
				new SeedSourceTemplateProvider(germplasmNamingProperties, new StudyTypeDto(StudyTypeDto.TRIAL_NAME), "wheat").getKeyTemplate());
	}
}
