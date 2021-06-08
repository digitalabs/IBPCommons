package org.generationcp.commons.service;

import org.generationcp.commons.ruleengine.RuleException;
import org.generationcp.commons.ruleengine.coding.CodingRuleExecutionContext;
import org.generationcp.middleware.domain.germplasm.GermplasmNameBatchRequestDto;
import org.generationcp.middleware.exceptions.InvalidGermplasmNameSettingException;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.germplasm.GermplasmNameSetting;
import org.generationcp.middleware.pojos.naming.NamingConfiguration;
import org.generationcp.middleware.service.api.GermplasmGroupNamingResult;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GermplasmCodeGenerationService {

	/**
	 * Generates code names for a list of Germplasm based on predefined NamingConfiguration.
	 *
	 * @param gidsToProcess
	 * @param namingConfiguration
	 * @param nameType
	 * @return
	 * @throws RuleException
	 */
	List<GermplasmGroupNamingResult> applyGroupNames(Set<Integer> gidsToProcess, NamingConfiguration namingConfiguration,
			UserDefinedField nameType) throws RuleException;

	/**
	 * Generates code names for a list of Germplasm based on user specified name setting (GermplasmNameSetting)
	 *
	 * @param gids
	 * @param setting
	 * @param nameType
	 * @param userId
	 * @param locationId
	 * @return
	 */
	List<GermplasmGroupNamingResult> applyGroupNames(Set<Integer> gids, GermplasmNameSetting setting, UserDefinedField nameType,
			Integer userId, Integer locationId);


	String getNextNameInSequence(final GermplasmNameSetting setting) throws InvalidGermplasmNameSettingException;

	List<GermplasmGroupNamingResult> createCodeNames( GermplasmNameBatchRequestDto germplasmNameBatchRequestDto) throws RuleException;

}
