package org.generationcp.commons.service.impl;

import org.generationcp.commons.ruleengine.RuleException;
import org.generationcp.commons.ruleengine.RuleFactory;
import org.generationcp.commons.ruleengine.coding.CodingRuleExecutionContext;
import org.generationcp.commons.ruleengine.service.RulesService;
import org.generationcp.commons.service.GermplasmCodeGenerationService;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.workbench.NamingConfiguration;
import org.generationcp.middleware.service.api.GermplasmGroupNamingResult;
import org.generationcp.middleware.service.api.GermplasmGroupingService;
import org.generationcp.middleware.service.api.GermplasmNamingService;
import org.generationcp.middleware.service.api.KeySequenceRegisterService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GermplasmCodeGenerationServiceImpl implements GermplasmCodeGenerationService {

	@Autowired
	private RulesService rulesService;

	@Autowired
	private RuleFactory ruleFactory;

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private GermplasmGroupingService germplasmGroupingService;

	@Autowired
	private GermplasmNamingService germplasmNamingService;

	@Autowired
	private KeySequenceRegisterService keySequenceRegisterService;

	@Override
	public Map<Integer, GermplasmGroupNamingResult> applyGroupNames(final Set<Integer> gidsToProcess,
			final NamingConfiguration namingConfiguration, final UserDefinedField nameType) throws RuleException {

		final String prefix = namingConfiguration.getPrefix();
		final String suffix = namingConfiguration.getSuffix();
		namingConfiguration.setSequenceCounter(this.keySequenceRegisterService.getNextSequence(prefix, suffix));

		List<String> executionOrder = Arrays.asList(this.ruleFactory.getRuleSequenceForNamespace("coding"));

		final CodingRuleExecutionContext codingRuleExecutionContext = new CodingRuleExecutionContext(executionOrder, namingConfiguration);

		final Map<Integer, GermplasmGroupNamingResult> assignCodesResultsMap = new LinkedHashMap<>();

		for (final Integer gid : gidsToProcess) {

			GermplasmGroupNamingResult result = new GermplasmGroupNamingResult();

			final Germplasm germplasm = this.germplasmDataManager.getGermplasmByGID(gid);

			if (germplasm.getMgid() == null || germplasm.getMgid() == 0) {
				result.addMessage(String.format("Germplasm (gid: %s) is not part of a management group. Can not assign group name.",
						germplasm.getGid()));
				assignCodesResultsMap.put(gid, result);
			}

			final List<Germplasm> groupMembers = this.germplasmGroupingService.getGroupMembers(germplasm.getMgid());
			final String generatedCodeName = (String) rulesService.runRules(codingRuleExecutionContext);

			for (final Germplasm member : groupMembers) {
				this.germplasmNamingService.addName(member, generatedCodeName, nameType, 0, 0, result);
			}

			assignCodesResultsMap.put(gid, result);

			codingRuleExecutionContext.reset();

		}

		final int lastSequenceUsed = namingConfiguration.getSequenceCounter() - 1;
		this.keySequenceRegisterService.saveLastSequenceUsed(prefix, suffix, lastSequenceUsed);

		return assignCodesResultsMap;
	}

}
