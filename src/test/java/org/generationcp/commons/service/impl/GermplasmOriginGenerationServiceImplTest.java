
package org.generationcp.commons.service.impl;

import junit.framework.Assert;

import org.generationcp.commons.service.GermplasmNamingProperties;
import org.generationcp.commons.service.GermplasmOriginGenerationParameters;
import org.generationcp.middleware.domain.oms.StudyType;
import org.junit.Test;

public class GermplasmOriginGenerationServiceImplTest {

	@Test
	public void testDefaults() {

		final GermplasmNamingProperties profile = new GermplasmNamingProperties();
		profile.setGermplasmOriginNurseriesDefault("[NAME]:[PLOTNO]");
		profile.setGermplasmOriginNurseriesWheat("[NAME]:[PLOTNO]");
		profile.setGermplasmOriginNurseriesMaize("[NAME]:[PLOTNO]");

		final GermplasmOriginGenerationParameters parameters = new GermplasmOriginGenerationParameters();

		parameters.setStudyName("StudyName");
		parameters.setLocation("IND");
		parameters.setPlotNumber("1");

		final GermplasmOriginGenerationServiceImpl service = new GermplasmOriginGenerationServiceImpl();
		service.setGermplasmNamingProperties(profile);

		parameters.setCrop("rice");
		Assert.assertEquals("StudyName:1", service.generateOriginString(parameters));

		parameters.setCrop("wheat");
		Assert.assertEquals("StudyName:1", service.generateOriginString(parameters));

		parameters.setCrop("maize");
		Assert.assertEquals("StudyName:1", service.generateOriginString(parameters));
	}

	@Test
	public void testDefaultsForCrossListImport() {

		final GermplasmNamingProperties profile = new GermplasmNamingProperties();
		profile.setGermplasmOriginNurseriesDefault("[NAME]:[PLOTNO]");
		profile.setGermplasmOriginNurseriesWheat("[NAME]:[PLOTNO]");
		profile.setGermplasmOriginNurseriesMaize("[NAME]:[PLOTNO]");

		final GermplasmOriginGenerationParameters parameters = new GermplasmOriginGenerationParameters();
		parameters.setCross(true);
		parameters.setMaleStudyName("Male Study Name");
		parameters.setMalePlotNumber("1");
		parameters.setFemaleStudyName("Female Study Name");
		parameters.setFemalePlotNumber("2");

		final GermplasmOriginGenerationServiceImpl service = new GermplasmOriginGenerationServiceImpl();
		service.setGermplasmNamingProperties(profile);

		parameters.setCrop("rice");
		Assert.assertEquals("Male Study Name:1/Female Study Name:2", service.generateOriginString(parameters));

		parameters.setCrop("wheat");
		Assert.assertEquals("Male Study Name:1/Female Study Name:2", service.generateOriginString(parameters));

		parameters.setCrop("maize");
		Assert.assertEquals("Male Study Name:1/Female Study Name:2", service.generateOriginString(parameters));
	}

	@Test
	public void testWheat() {

		final GermplasmNamingProperties profile = new GermplasmNamingProperties();
		profile.setGermplasmOriginNurseriesWheat("[LOCATION]\\[SEASON]\\[NAME]\\[PLOTNO]");

		final GermplasmOriginGenerationParameters parameters = new GermplasmOriginGenerationParameters();
		parameters.setCrop("wheat");
		parameters.setStudyName("Wheat Study");
		parameters.setLocation("IND");
		parameters.setPlotNumber("1");
		parameters.setSeason("Summer");

		final GermplasmOriginGenerationServiceImpl service = new GermplasmOriginGenerationServiceImpl();
		service.setGermplasmNamingProperties(profile);

		Assert.assertEquals("IND\\Summer\\Wheat Study\\1", service.generateOriginString(parameters));
	}

	@Test
	public void testWheatForCrossListImport() {

		final GermplasmNamingProperties profile = new GermplasmNamingProperties();
		profile.setGermplasmOriginNurseriesWheat("[LOCATION]\\[SEASON]\\[NAME]\\[PLOTNO]");

		final GermplasmOriginGenerationParameters parameters = new GermplasmOriginGenerationParameters();
		parameters.setCrop("wheat");
		parameters.setLocation("IND");
		parameters.setSeason("Summer");
		parameters.setCross(true);
		parameters.setMaleStudyName("Male Study Name");
		parameters.setMalePlotNumber("1");
		parameters.setFemaleStudyName("Female Study Name");
		parameters.setFemalePlotNumber("2");

		final GermplasmOriginGenerationServiceImpl service = new GermplasmOriginGenerationServiceImpl();
		service.setGermplasmNamingProperties(profile);

		Assert.assertEquals("IND\\Summer\\Male Study Name\\1/IND\\Summer\\Female Study Name\\2", service.generateOriginString(parameters));
	}

	@Test
	public void testMaize() {

		final GermplasmNamingProperties profile = new GermplasmNamingProperties();
		profile.setGermplasmOriginNurseriesMaize("[LOCATION][SEASON]-[NAME]-[PLOTNO]");

		final GermplasmOriginGenerationParameters parameters = new GermplasmOriginGenerationParameters();
		parameters.setCrop("maize");
		parameters.setStudyName("Maize Study");
		parameters.setLocation("IND");
		parameters.setPlotNumber("1");
		parameters.setSeason("Winter");

		final GermplasmOriginGenerationServiceImpl service = new GermplasmOriginGenerationServiceImpl();
		service.setGermplasmNamingProperties(profile);

		parameters.setStudyType(StudyType.N);
		Assert.assertEquals("INDWinter-Maize Study-1", service.generateOriginString(parameters));

		parameters.setStudyType(StudyType.T);
		Assert.assertEquals("INDWinter-Maize Study-1", service.generateOriginString(parameters));
	}

	@Test
	public void testMaizeForCrossListImport() {

		final GermplasmNamingProperties profile = new GermplasmNamingProperties();
		profile.setGermplasmOriginNurseriesMaize("[LOCATION][SEASON]-[NAME]-[PLOTNO]");

		final GermplasmOriginGenerationParameters parameters = new GermplasmOriginGenerationParameters();
		parameters.setCrop("maize");
		parameters.setLocation("IND");
		parameters.setSeason("Summer");
		parameters.setCross(true);
		parameters.setMaleStudyName("Male Study Name");
		parameters.setMalePlotNumber("1");
		parameters.setFemaleStudyName("Female Study Name");
		parameters.setFemalePlotNumber("2");

		final GermplasmOriginGenerationServiceImpl service = new GermplasmOriginGenerationServiceImpl();
		service.setGermplasmNamingProperties(profile);

		Assert.assertEquals("INDSummer-Male Study Name-1/INDSummer-Female Study Name-2", service.generateOriginString(parameters));
	}

}
