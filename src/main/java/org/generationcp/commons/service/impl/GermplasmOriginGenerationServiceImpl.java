
package org.generationcp.commons.service.impl;

import org.generationcp.commons.service.GermplasmNamingProperties;
import org.generationcp.commons.service.GermplasmOriginGenerationParameters;
import org.generationcp.commons.service.GermplasmOriginGenerationService;
import org.generationcp.middleware.domain.oms.StudyType;

import com.google.common.base.Strings;

public class GermplasmOriginGenerationServiceImpl implements GermplasmOriginGenerationService {

	private GermplasmNamingProperties germplasmNamingProperties;

	@Override
	public String generateOriginString(GermplasmOriginGenerationParameters parameters) {
		String formatToUse = this.determineFormatStringToUse(parameters);
		return formatToUse
				.replace("{NAME}", Strings.nullToEmpty(parameters.getStudyName()))
				.replace("{LOCATION}", Strings.nullToEmpty(parameters.getLocation()))
				.replace("{SEASON}", Strings.nullToEmpty(parameters.getSeason()))
				.replace("{PLOTNO}", Strings.nullToEmpty(parameters.getPlotNumber()));
	}

	private String determineFormatStringToUse(GermplasmOriginGenerationParameters namingParameters) {

		String formatToUse =
				namingParameters.getStudyType() == StudyType.N ? this.germplasmNamingProperties.getGermplasmOriginNurseriesDefault()
						: this.germplasmNamingProperties.getGermplasmOriginTrialsDefault();

		if (namingParameters.getCrop().equals("wheat")) {
			formatToUse =
					namingParameters.getStudyType() == StudyType.N ? this.germplasmNamingProperties.getGermplasmOriginNurseriesWheat()
							: this.germplasmNamingProperties.getGermplasmOriginTrialsWheat();
		} else if (namingParameters.getCrop().equals("maize")) {
			formatToUse =
					namingParameters.getStudyType() == StudyType.N ? this.germplasmNamingProperties.getGermplasmOriginNurseriesMaize()
							: this.germplasmNamingProperties.getGermplasmOriginTrialsMaize();
		}

		return formatToUse;
	}

	public void setGermplasmNamingProperties(GermplasmNamingProperties germplasmNamingProperties) {
		this.germplasmNamingProperties = germplasmNamingProperties;
	}
}
