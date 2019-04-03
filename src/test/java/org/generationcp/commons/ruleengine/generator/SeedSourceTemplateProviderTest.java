package org.generationcp.commons.ruleengine.generator;

import junit.framework.Assert;
import org.generationcp.commons.ruleengine.generator.SeedSourceTemplateProvider;
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
				new SeedSourceTemplateProvider(germplasmNamingProperties, "rice").getKeyTemplate());

		Assert.assertEquals(germplasmNamingProperties.getGermplasmOriginStudiesMaize(),
				new SeedSourceTemplateProvider(germplasmNamingProperties, "maize").getKeyTemplate());

		Assert.assertEquals(germplasmNamingProperties.getGermplasmOriginStudiesWheat(),
				new SeedSourceTemplateProvider(germplasmNamingProperties, "wheat").getKeyTemplate());

		// Studies
		Assert.assertEquals(germplasmNamingProperties.getGermplasmOriginStudiesDefault(),
				new SeedSourceTemplateProvider(germplasmNamingProperties, "rice").getKeyTemplate());

		Assert.assertEquals(germplasmNamingProperties.getGermplasmOriginStudiesMaize(),
				new SeedSourceTemplateProvider(germplasmNamingProperties, "maize").getKeyTemplate());

		Assert.assertEquals(germplasmNamingProperties.getGermplasmOriginStudiesWheat(),
				new SeedSourceTemplateProvider(germplasmNamingProperties, "wheat").getKeyTemplate());
	}
}
