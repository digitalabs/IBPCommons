
package org.generationcp.commons.ruleengine.generator;

import org.generationcp.commons.service.GermplasmNamingProperties;
import org.generationcp.commons.service.KeyTemplateProvider;
import org.generationcp.middleware.domain.study.StudyTypeDto;

/**
 * Locates the key code templates for seed source (currently, from properties file, in future this may be loaded from database configuration
 * service).
 * 
 */
public class SeedSourceTemplateProvider implements KeyTemplateProvider {

	public static final String WHEAT = "wheat";
	public static final String MAIZE = "maize";
	private GermplasmNamingProperties germplasmNamingProperties;
	private final StudyTypeDto studyType;
	private final String crop;

	public SeedSourceTemplateProvider(final GermplasmNamingProperties germplasmNamingProperties, final StudyTypeDto studyType, final String crop) {
		this.germplasmNamingProperties = germplasmNamingProperties;
		this.studyType = studyType;
		this.crop = crop;
	}

	@Override
	public String getKeyTemplate() {
		String seedSourceTemplate;

		seedSourceTemplate = this.germplasmNamingProperties.getGermplasmOriginStudiesDefault();

		if (this.crop.equals(WHEAT)) {
			seedSourceTemplate = this.germplasmNamingProperties.getGermplasmOriginStudiesWheat();
		} else if (this.crop.equals(MAIZE)) {
			seedSourceTemplate = this.germplasmNamingProperties.getGermplasmOriginStudiesMaize();
		} else {
			seedSourceTemplate = this.germplasmNamingProperties.getGermplasmOriginStudiesDefault();
		}
		return seedSourceTemplate;
	}

	public void setGermplasmNamingProperties(final GermplasmNamingProperties germplasmNamingProperties) {
		this.germplasmNamingProperties = germplasmNamingProperties;
	}

}
