package org.generationcp.commons.ruleengine.generator;

import org.generationcp.commons.service.GermplasmNamingProperties;
import org.generationcp.commons.service.KeyTemplateProvider;

public class BreedersCrossIDTemplateProvider implements KeyTemplateProvider{

	private final GermplasmNamingProperties germplasmNamingProperties;

	public BreedersCrossIDTemplateProvider(final GermplasmNamingProperties germplasmNamingProperties){
		this.germplasmNamingProperties = germplasmNamingProperties;
	}

	@Override
	public String getKeyTemplate() {
		String breedersCrossIDTemplate = "";
		breedersCrossIDTemplate = this.germplasmNamingProperties.getBreedersCrossIDStudy();
		return breedersCrossIDTemplate;
	}
}
