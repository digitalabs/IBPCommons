package org.generationcp.commons.service.impl;

import junit.framework.Assert;
import org.generationcp.commons.service.GermplasmNamingProperties;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.junit.Test;

public class SeedSourceTemplateProviderTest {

	@Test
	public void testGetKeyTemplate() {

		final GermplasmNamingProperties germplasmNamingProperties = new GermplasmNamingProperties();
		germplasmNamingProperties.setGermplasmOriginNurseriesDefault("[NAME]:[PLOTNO]");
		germplasmNamingProperties.setGermplasmOriginNurseriesMaize("[LOCATION][SEASON]-[NAME]-[PLOTNO][SELECTION_NUMBER]");
		germplasmNamingProperties.setGermplasmOriginNurseriesWheat("[LOCATION]\\[SEASON]\\[NAME]\\[PLOTNO]");

		germplasmNamingProperties.setGermplasmOriginTrialsDefault("[NAME]:[LOCATION]:[SEASON]:[PLOTNO]");
		germplasmNamingProperties.setGermplasmOriginTrialsMaize("[LOCATION]\\[SEASON]\\[NAME]\\[PLOTNO]");
		germplasmNamingProperties.setGermplasmOriginTrialsWheat("[LOCATION][SEASON]-[NAME]-[PLOTNO]");

		// Nurseries
		Assert.assertEquals(germplasmNamingProperties.getGermplasmOriginNurseriesDefault(),
				new SeedSourceTemplateProvider(germplasmNamingProperties, new StudyTypeDto("N"), "rice").getKeyTemplate());

		Assert.assertEquals(germplasmNamingProperties.getGermplasmOriginNurseriesMaize(),
				new SeedSourceTemplateProvider(germplasmNamingProperties, new StudyTypeDto("N"), "maize").getKeyTemplate());

		Assert.assertEquals(germplasmNamingProperties.getGermplasmOriginNurseriesWheat(),
				new SeedSourceTemplateProvider(germplasmNamingProperties, new StudyTypeDto("N"), "wheat").getKeyTemplate());

		// Trials
		Assert.assertEquals(germplasmNamingProperties.getGermplasmOriginTrialsDefault(),
				new SeedSourceTemplateProvider(germplasmNamingProperties, new StudyTypeDto("T"), "rice").getKeyTemplate());

		Assert.assertEquals(germplasmNamingProperties.getGermplasmOriginTrialsMaize(),
				new SeedSourceTemplateProvider(germplasmNamingProperties, new StudyTypeDto("T"), "maize").getKeyTemplate());

		Assert.assertEquals(germplasmNamingProperties.getGermplasmOriginTrialsWheat(),
				new SeedSourceTemplateProvider(germplasmNamingProperties, new StudyTypeDto("T"), "wheat").getKeyTemplate());
	}
}
