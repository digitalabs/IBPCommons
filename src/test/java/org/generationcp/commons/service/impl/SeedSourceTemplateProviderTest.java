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
				new SeedSourceTemplateProvider(germplasmNamingProperties, StudyTypeDto.getNurseryDto(), "rice").getKeyTemplate());

		Assert.assertEquals(germplasmNamingProperties.getGermplasmOriginStudiesMaize(),
				new SeedSourceTemplateProvider(germplasmNamingProperties, StudyTypeDto.getNurseryDto(), "maize").getKeyTemplate());

		Assert.assertEquals(germplasmNamingProperties.getGermplasmOriginStudiesWheat(),
				new SeedSourceTemplateProvider(germplasmNamingProperties, StudyTypeDto.getNurseryDto(), "wheat").getKeyTemplate());

		// Studies
		Assert.assertEquals(germplasmNamingProperties.getGermplasmOriginStudiesDefault(),
				new SeedSourceTemplateProvider(germplasmNamingProperties, StudyTypeDto.getTrialDto(), "rice").getKeyTemplate());

		Assert.assertEquals(germplasmNamingProperties.getGermplasmOriginStudiesMaize(),
				new SeedSourceTemplateProvider(germplasmNamingProperties, StudyTypeDto.getTrialDto(), "maize").getKeyTemplate());

		Assert.assertEquals(germplasmNamingProperties.getGermplasmOriginStudiesWheat(),
				new SeedSourceTemplateProvider(germplasmNamingProperties, StudyTypeDto.getTrialDto(), "wheat").getKeyTemplate());
	}
}
