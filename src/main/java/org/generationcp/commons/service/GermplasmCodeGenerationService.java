package org.generationcp.commons.service;

import org.generationcp.commons.ruleengine.RuleException;
import org.generationcp.middleware.exceptions.InvalidGermplasmNameSettingException;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.germplasm.GermplasmNameSetting;
import org.generationcp.middleware.pojos.workbench.NamingConfiguration;
import org.generationcp.middleware.service.api.GermplasmGroupNamingResult;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

public interface GermplasmCodeGenerationService {

	/**
	 * Generates code names for a list of Germplasm based on predefined NamingConfiguration.
	 * @param gidsToProcess
	 * @param namingConfiguration
	 * @param nameType
	 * @return
	 * @throws RuleException
	 */
	Map<Integer, GermplasmGroupNamingResult> applyGroupNames(Set<Integer> gidsToProcess, NamingConfiguration namingConfiguration,
			UserDefinedField nameType) throws RuleException;

	@Transactional(propagation = Propagation.MANDATORY)
	GermplasmGroupNamingResult applyGroupName(Integer gid, GermplasmNameSetting setting, UserDefinedField nameType, Integer userId,
			Integer locationId);

	/**
	 * Generates code names for a list of Germplasm based on user specified name setting (GermplasmNameSetting)
	 * @param gids
	 * @param setting
	 * @param nameType
	 * @param userId
	 * @param locationId
	 * @return
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	Map<Integer, GermplasmGroupNamingResult> applyGroupNames(Set<Integer> gids, GermplasmNameSetting setting, UserDefinedField nameType,
			Integer userId, Integer locationId);


	String getNextNameInSequence(GermplasmNameSetting setting) throws InvalidGermplasmNameSettingException;
}
