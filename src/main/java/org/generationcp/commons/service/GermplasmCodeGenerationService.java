package org.generationcp.commons.service;

import org.generationcp.commons.ruleengine.RuleException;
import org.generationcp.middleware.domain.germplasm.GermplasmCodeNameBatchRequestDto;
import org.generationcp.middleware.exceptions.InvalidGermplasmNameSettingException;
import org.generationcp.middleware.pojos.germplasm.GermplasmNameSetting;
import org.generationcp.middleware.service.api.GermplasmGroupNamingResult;

import java.util.List;

public interface GermplasmCodeGenerationService {

	String getNextNameInSequence(final GermplasmNameSetting setting) throws InvalidGermplasmNameSettingException;

	List<GermplasmGroupNamingResult> createCodeNames(GermplasmCodeNameBatchRequestDto germplasmCodeNameBatchRequestDto) throws RuleException;

}
