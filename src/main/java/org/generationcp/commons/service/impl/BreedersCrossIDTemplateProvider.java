package org.generationcp.commons.service.impl;

import org.generationcp.commons.service.GermplasmNamingProperties;
import org.generationcp.commons.service.KeyTemplateProvider;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.study.StudyTypeDto;

public class BreedersCrossIDTemplateProvider implements KeyTemplateProvider{

	private final GermplasmNamingProperties germplasmNamingProperties;
	private final StudyTypeDto studyType;

	public BreedersCrossIDTemplateProvider(final GermplasmNamingProperties germplasmNamingProperties, final StudyTypeDto studyType){
		this.germplasmNamingProperties = germplasmNamingProperties;
		this.studyType = studyType;
	}

	@Override
	public String getKeyTemplate() {
		String breedersCrossIDTemplate = "";

		if (this.studyType.equals(StudyType.N)) {
			breedersCrossIDTemplate = this.germplasmNamingProperties.getBreedersCrossIDNursery();
		} else if (this.studyType.equals(StudyType.T)) {
			breedersCrossIDTemplate = this.germplasmNamingProperties.getBreedersCrossIDTrial();
		}
		return breedersCrossIDTemplate;
	}
}
