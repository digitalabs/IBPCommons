package org.generationcp.commons.service;

import org.generationcp.commons.ruleengine.RuleException;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.workbench.NamingConfiguration;
import org.generationcp.middleware.service.api.GermplasmGroupNamingResult;

import java.util.Map;
import java.util.Set;

public interface GermplasmCodeGenerationService {

	Map<Integer, GermplasmGroupNamingResult> applyGroupNames(Set<Integer> gidsToProcess, NamingConfiguration namingConfiguration,
			UserDefinedField nameType) throws RuleException;
}
