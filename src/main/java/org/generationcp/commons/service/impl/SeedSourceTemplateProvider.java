
package org.generationcp.commons.service.impl;

import org.generationcp.commons.service.GermplasmNamingProperties;
import org.generationcp.commons.service.KeyTemplateProvider;
import org.generationcp.middleware.domain.oms.StudyType;

/**
 * Locates the key code templates for seed source (currently, from properties file, in future this may be loaded from database configuration
 * service).
 * 
 */
public class SeedSourceTemplateProvider implements KeyTemplateProvider {

	private GermplasmNamingProperties germplasmNamingProperties;
	private StudyType studyType;
	private String crop;

	public SeedSourceTemplateProvider(GermplasmNamingProperties germplasmNamingProperties, StudyType studyType, String crop) {
		this.germplasmNamingProperties = germplasmNamingProperties;
		this.studyType = studyType;
		this.crop = crop;
	}

	@Override
	public String getKeyTemplate() {
		String seedSourceTemplate = "";
		if (this.studyType.equals(StudyType.N)) {
			seedSourceTemplate = this.germplasmNamingProperties.getGermplasmOriginNurseriesDefault();
			if (this.crop.equals("wheat")) {
				seedSourceTemplate = this.germplasmNamingProperties.getGermplasmOriginNurseriesWheat();
			} else if (this.crop.equals("maize")) {
				seedSourceTemplate = this.germplasmNamingProperties.getGermplasmOriginNurseriesMaize();
			}
		} else if (this.studyType.equals(StudyType.T)) {
			seedSourceTemplate = this.germplasmNamingProperties.getGermplasmOriginTrialsDefault();

			if (this.crop.equals("wheat")) {
				seedSourceTemplate = this.germplasmNamingProperties.getGermplasmOriginTrialsWheat();
			} else if (this.crop.equals("maize")) {
				seedSourceTemplate = this.germplasmNamingProperties.getGermplasmOriginTrialsMaize();
			}
		}
		return seedSourceTemplate;
	}

	public void setGermplasmNamingProperties(GermplasmNamingProperties germplasmNamingProperties) {
		this.germplasmNamingProperties = germplasmNamingProperties;
	}

}
