
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
			final String seedSource = formatToUse.replace("[NAME]", Strings.nullToEmpty(parameters.getStudyName()))
					.replace("[LOCATION]", Strings.nullToEmpty(parameters.getLocation()))
					.replace("[SEASON]", Strings.nullToEmpty(parameters.getSeason()))
					.replace("[PLOTNO]", Strings.nullToEmpty(parameters.getPlotNumber()));
			
			if (!Strings.isNullOrEmpty(parameters.getSelectionNumber())) {
				return seedSource.replace("[SELECTION_NUMBER]", "-" + parameters.getSelectionNumber());
			} else {
				return seedSource.replace("[SELECTION_NUMBER]", "");
			}
		}
	}

	private String determineFormatStringToUse(GermplasmOriginGenerationParameters namingParameters) {
		String formatToUse = "";
		if (namingParameters.getStudyType().equals(StudyType.N)) {
			formatToUse = this.germplasmNamingProperties.getGermplasmOriginNurseriesDefault();

			if (namingParameters.getCrop().equals("wheat")) {
				formatToUse = this.germplasmNamingProperties.getGermplasmOriginNurseriesWheat();
			} else if (namingParameters.getCrop().equals("maize")) {
				formatToUse = this.germplasmNamingProperties.getGermplasmOriginNurseriesMaize();
			}
		} else if (namingParameters.getStudyType().equals(StudyType.T)) {
			formatToUse = this.germplasmNamingProperties.getGermplasmOriginTrialsDefault();

			if (namingParameters.getCrop().equals("wheat")) {
				formatToUse = this.germplasmNamingProperties.getGermplasmOriginTrialsWheat();
			} else if (namingParameters.getCrop().equals("maize")) {
				formatToUse = this.germplasmNamingProperties.getGermplasmOriginTrialsMaize();
			}
		}
		return formatToUse;
	}

	public void setGermplasmNamingProperties(GermplasmNamingProperties germplasmNamingProperties) {
		this.germplasmNamingProperties = germplasmNamingProperties;
	}
}
