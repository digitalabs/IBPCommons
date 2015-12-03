package org.generationcp.commons.service.impl;

import junit.framework.Assert;

import org.generationcp.commons.service.GermplasmNamingProperties;
import org.generationcp.commons.service.GermplasmOriginGenerationParameters;
import org.generationcp.middleware.domain.oms.StudyType;
import org.junit.Test;

public class GermplasmOriginGenerationServiceImplTest {

	@Test
	public void testDefaults() {

		GermplasmNamingProperties profile = new GermplasmNamingProperties();
		profile.setGermplasmOriginTrialsDefault("{NAME}:{LOCATION}:{PLOTNO}");
		profile.setGermplasmOriginNurseriesDefault("{NAME}:{PLOTNO}");

		profile.setGermplasmOriginTrialsWheat("{NAME}:{LOCATION}:{PLOTNO}");
		profile.setGermplasmOriginNurseriesWheat("{NAME}:{PLOTNO}");

		profile.setGermplasmOriginTrialsMaize("{NAME}:{LOCATION}:{PLOTNO}");
		profile.setGermplasmOriginNurseriesMaize("{NAME}:{PLOTNO}");

		GermplasmOriginGenerationParameters parameters = new GermplasmOriginGenerationParameters();

		parameters.setStudyName("StudyName");
		parameters.setLocation("IND");
		parameters.setPlotNumber("1");

		GermplasmOriginGenerationServiceImpl service = new GermplasmOriginGenerationServiceImpl();
		service.setGermplasmNamingProperties(profile);

		parameters.setCrop("rice");
		parameters.setStudyType(StudyType.N);
		Assert.assertEquals("StudyName:1", service.generateOriginString(parameters));

		parameters.setCrop("rice");
		parameters.setStudyType(StudyType.T);
		Assert.assertEquals("StudyName:IND:1", service.generateOriginString(parameters));

		parameters.setCrop("wheat");
		parameters.setStudyType(StudyType.N);
		Assert.assertEquals("StudyName:1", service.generateOriginString(parameters));

		parameters.setCrop("wheat");
		parameters.setStudyType(StudyType.T);
		Assert.assertEquals("StudyName:IND:1", service.generateOriginString(parameters));

		parameters.setCrop("maize");
		parameters.setStudyType(StudyType.N);
		Assert.assertEquals("StudyName:1", service.generateOriginString(parameters));

		parameters.setCrop("maize");
		parameters.setStudyType(StudyType.T);
		Assert.assertEquals("StudyName:IND:1", service.generateOriginString(parameters));
	}

	@Test
	public void testWheat() {

		GermplasmNamingProperties profile = new GermplasmNamingProperties();
		profile.setGermplasmOriginTrialsWheat("{LOCATION}{SEASON}\\{NAME}\\{PLOTNO}");
		profile.setGermplasmOriginNurseriesWheat("{LOCATION}{SEASON}\\{NAME}\\{PLOTNO}");

		GermplasmOriginGenerationParameters parameters = new GermplasmOriginGenerationParameters();
		parameters.setCrop("wheat");
		parameters.setStudyName("Wheat Study");
		parameters.setLocation("IND");
		parameters.setPlotNumber("1");
		parameters.setSeason("Summer");

		GermplasmOriginGenerationServiceImpl service = new GermplasmOriginGenerationServiceImpl();
		service.setGermplasmNamingProperties(profile);

		parameters.setStudyType(StudyType.N);
		Assert.assertEquals("INDSummer\\Wheat Study\\1", service.generateOriginString(parameters));

		parameters.setStudyType(StudyType.T);
		Assert.assertEquals("INDSummer\\Wheat Study\\1", service.generateOriginString(parameters));
	}

	@Test
	public void testMaize() {

		GermplasmNamingProperties profile = new GermplasmNamingProperties();
		profile.setGermplasmOriginTrialsMaize("{LOCATION}{SEASON}-{NAME}-{PLOTNO}");
		profile.setGermplasmOriginNurseriesMaize("{LOCATION}{SEASON}-{NAME}-{PLOTNO}");

		GermplasmOriginGenerationParameters parameters = new GermplasmOriginGenerationParameters();
		parameters.setCrop("maize");
		parameters.setStudyName("Maize Study");
		parameters.setLocation("IND");
		parameters.setPlotNumber("1");
		parameters.setSeason("Winter");

		GermplasmOriginGenerationServiceImpl service = new GermplasmOriginGenerationServiceImpl();
		service.setGermplasmNamingProperties(profile);

		parameters.setStudyType(StudyType.N);
		Assert.assertEquals("INDWinter-Maize Study-1", service.generateOriginString(parameters));

		parameters.setStudyType(StudyType.T);
		Assert.assertEquals("INDWinter-Maize Study-1", service.generateOriginString(parameters));
	}

}
