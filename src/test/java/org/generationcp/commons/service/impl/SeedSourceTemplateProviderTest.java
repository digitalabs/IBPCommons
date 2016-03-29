package org.generationcp.commons.service.impl;

import org.generationcp.commons.service.GermplasmNamingProperties;
import org.generationcp.middleware.domain.oms.StudyType;
import org.junit.Test;

import junit.framework.Assert;

public class SeedSourceTemplateProviderTest {

	@Test
	public void testGetKeyTemplate() {

		GermplasmNamingProperties germplasmNamingProperties = new GermplasmNamingProperties();
		germplasmNamingProperties.setGermplasmOriginNurseriesDefault("[NAME]:[PLOTNO]");
		germplasmNamingProperties.setGermplasmOriginNurseriesMaize("[LOCATION][SEASON]-[NAME]-[PLOTNO][SELECTION_NUMBER]");
		germplasmNamingProperties.setGermplasmOriginNurseriesWheat("[LOCATION]\\[SEASON]\\[NAME]\\[PLOTNO]");

		germplasmNamingProperties.setGermplasmOriginTrialsDefault("[NAME]:[LOCATION]:[SEASON]:[PLOTNO]");
		germplasmNamingProperties.setGermplasmOriginTrialsMaize("[LOCATION]\\[SEASON]\\[NAME]\\[PLOTNO]");
		germplasmNamingProperties.setGermplasmOriginTrialsWheat("[LOCATION][SEASON]-[NAME]-[PLOTNO]");

		// Nurseries
		Assert.assertEquals(germplasmNamingProperties.getGermplasmOriginNurseriesDefault(),
				new SeedSourceTemplateProvider(germplasmNamingProperties, StudyType.N, "rice").getKeyTemplate());

		Assert.assertEquals(germplasmNamingProperties.getGermplasmOriginNurseriesMaize(),
				new SeedSourceTemplateProvider(germplasmNamingProperties, StudyType.N, "maize").getKeyTemplate());

		Assert.assertEquals(germplasmNamingProperties.getGermplasmOriginNurseriesWheat(),
				new SeedSourceTemplateProvider(germplasmNamingProperties, StudyType.N, "wheat").getKeyTemplate());

		// Trials
		Assert.assertEquals(germplasmNamingProperties.getGermplasmOriginTrialsDefault(),
				new SeedSourceTemplateProvider(germplasmNamingProperties, StudyType.T, "rice").getKeyTemplate());

		Assert.assertEquals(germplasmNamingProperties.getGermplasmOriginTrialsMaize(),
				new SeedSourceTemplateProvider(germplasmNamingProperties, StudyType.T, "maize").getKeyTemplate());

		Assert.assertEquals(germplasmNamingProperties.getGermplasmOriginTrialsWheat(),
				new SeedSourceTemplateProvider(germplasmNamingProperties, StudyType.T, "wheat").getKeyTemplate());
	}
}
