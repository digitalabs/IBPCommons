package org.generationcp.commons.service.impl;

import org.generationcp.commons.service.GermplasmNamingProperties;
import org.generationcp.commons.service.KeyTemplateProvider;
import org.generationcp.middleware.domain.oms.StudyType;

public class BreedersCrossIDTemplateProvider implements KeyTemplateProvider{

	private GermplasmNamingProperties germplasmNamingProperties;
	private StudyType studyType;

	public BreedersCrossIDTemplateProvider(GermplasmNamingProperties germplasmNamingProperties, StudyType studyType){
		this.germplasmNamingProperties = germplasmNamingProperties;
		this.studyType = studyType;
	}

	@Override
	public String getKeyTemplate() {
		String breedersCrossIDTemplate = "";

		if (this.studyType.equals(StudyType.N)) {
			breedersCrossIDTemplate = this.germplasmNamingProperties.getBreedersCrossIDOriginNursery();
		} else if (this.studyType.equals(StudyType.T)) {
			breedersCrossIDTemplate = this.germplasmNamingProperties.getBreedersCrossIDOriginTrial();
		}
		return breedersCrossIDTemplate;
	}
}
