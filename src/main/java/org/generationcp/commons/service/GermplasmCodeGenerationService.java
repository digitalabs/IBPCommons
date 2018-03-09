package org.generationcp.commons.service;

import org.generationcp.commons.ruleengine.RuleException;
import org.generationcp.commons.ruleengine.coding.CodingRuleExecutionContext;
import org.generationcp.middleware.exceptions.InvalidGermplasmNameSettingException;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.germplasm.GermplasmNameSetting;
import org.generationcp.middleware.pojos.workbench.NamingConfiguration;
import org.generationcp.middleware.service.api.GermplasmGroupNamingResult;

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
	Map<Integer, GermplasmGroupNamingResult> applyGroupNames(Set<Integer> gidsToProcess, NamingConfiguration namingConfiguration,
			UserDefinedField nameType) throws RuleException;

	/**
	 * Generates code name for the Germplasm based on predefined NamingConfiguration.
	 *
	 * @param gidsToProcess
	 * @param namingConfiguration
	 * @param nameType
	 * @return
	 * @throws RuleException
	 */
	GermplasmGroupNamingResult applyGroupName(Integer gid, NamingConfiguration namingConfiguration, UserDefinedField nameType,
			CodingRuleExecutionContext codingRuleExecutionContext) throws RuleException;

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
	Map<Integer, GermplasmGroupNamingResult> applyGroupNames(Set<Integer> gids, GermplasmNameSetting setting, UserDefinedField nameType,
			Integer userId, Integer locationId);

	/**
	 * Generates code name for the Germplasm based on user specified name setting (GermplasmNameSetting)
	 *
	 * @param gids
	 * @param setting
	 * @param nameType
	 * @param userId
	 * @param locationId
	 * @return
	 */
	GermplasmGroupNamingResult applyGroupName(Integer gid, GermplasmNameSetting setting, UserDefinedField nameType, Integer userId,
			Integer locationId);

	String getNextNameInSequence(GermplasmNameSetting setting) throws InvalidGermplasmNameSettingException;
}
