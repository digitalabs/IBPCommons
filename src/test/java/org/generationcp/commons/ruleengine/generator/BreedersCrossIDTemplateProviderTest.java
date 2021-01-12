package org.generationcp.commons.ruleengine.generator;

import org.junit.Assert;
import org.generationcp.commons.service.GermplasmNamingProperties;
import org.junit.Test;

public class BreedersCrossIDTemplateProviderTest {

	@Test
	public void testGetKeyTemplate() {

		final GermplasmNamingProperties germplasmNamingProperties = new GermplasmNamingProperties();

		germplasmNamingProperties.setBreedersCrossIDStudy("[PROJECT_PREFIX][HABITAT_DESIGNATION]-[SEASON]-[LOCATION]");
		germplasmNamingProperties.setBreedersCrossIDStudy("[PROJECT_PREFIX][HABITAT_DESIGNATION]-[SEASON]:[LOCATION]");

		// Study
		Assert.assertEquals(germplasmNamingProperties.getBreedersCrossIDStudy(),
				new BreedersCrossIDTemplateProvider(germplasmNamingProperties).getKeyTemplate());

		// Study
		Assert.assertEquals(germplasmNamingProperties.getBreedersCrossIDStudy(),
				new BreedersCrossIDTemplateProvider(germplasmNamingProperties).getKeyTemplate());
	}
}
