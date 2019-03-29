package org.generationcp.commons.ruleengine.generator;

import org.generationcp.commons.service.GermplasmNamingProperties;
import org.generationcp.commons.service.KeyTemplateProvider;
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
		breedersCrossIDTemplate = this.germplasmNamingProperties.getBreedersCrossIDStudy();
		return breedersCrossIDTemplate;
	}
}
