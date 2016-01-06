
package org.generationcp.commons.service.impl;

import org.generationcp.commons.service.GermplasmNamingProperties;
import org.generationcp.commons.service.GermplasmOriginGenerationParameters;
import org.generationcp.commons.service.GermplasmOriginGenerationService;

import com.google.common.base.Strings;

public class GermplasmOriginGenerationServiceImpl implements GermplasmOriginGenerationService {

	private GermplasmNamingProperties germplasmNamingProperties;

	@Override
	public String generateOriginString(GermplasmOriginGenerationParameters parameters) {
		String formatToUse = this.determineFormatStringToUse(parameters);
		
		if (parameters.isCross()) {
			String maleString =
					formatToUse.replace("[NAME]", Strings.nullToEmpty(parameters.getMaleStudyName()))
							.replace("[LOCATION]", Strings.nullToEmpty(parameters.getLocation()))
							.replace("[SEASON]", Strings.nullToEmpty(parameters.getSeason()))
							.replace("[PLOTNO]", Strings.nullToEmpty(parameters.getMalePlotNumber()));

			String femaleString =
					formatToUse.replace("[NAME]", Strings.nullToEmpty(parameters.getFemaleStudyName()))
							.replace("[LOCATION]", Strings.nullToEmpty(parameters.getLocation()))
							.replace("[SEASON]", Strings.nullToEmpty(parameters.getSeason()))
							.replace("[PLOTNO]", Strings.nullToEmpty(parameters.getFemalePlotNumber()));

			return femaleString + "/" + maleString;
		} else {
			return formatToUse.replace("[NAME]", Strings.nullToEmpty(parameters.getStudyName()))
					.replace("[LOCATION]", Strings.nullToEmpty(parameters.getLocation()))
					.replace("[SEASON]", Strings.nullToEmpty(parameters.getSeason()))
					.replace("[PLOTNO]", Strings.nullToEmpty(parameters.getPlotNumber()));
		}
	}

	private String determineFormatStringToUse(GermplasmOriginGenerationParameters namingParameters) {

		String formatToUse = this.germplasmNamingProperties.getGermplasmOriginNurseriesDefault();

		if (namingParameters.getCrop().equals("wheat")) {
			formatToUse = this.germplasmNamingProperties.getGermplasmOriginNurseriesWheat();
		} else if (namingParameters.getCrop().equals("maize")) {
			formatToUse = this.germplasmNamingProperties.getGermplasmOriginNurseriesMaize();
		}

		return formatToUse;
	}

	public void setGermplasmNamingProperties(GermplasmNamingProperties germplasmNamingProperties) {
		this.germplasmNamingProperties = germplasmNamingProperties;
	}
}
