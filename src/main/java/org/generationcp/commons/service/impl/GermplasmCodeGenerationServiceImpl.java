package org.generationcp.commons.service.impl;

import org.generationcp.commons.ruleengine.RuleException;
import org.generationcp.commons.ruleengine.RuleFactory;
import org.generationcp.commons.ruleengine.coding.CodingRuleExecutionContext;
import org.generationcp.commons.ruleengine.service.RulesService;
import org.generationcp.commons.service.GermplasmCodeGenerationService;
import org.generationcp.commons.service.GermplasmNamingService;
import org.generationcp.middleware.exceptions.InvalidGermplasmNameSettingException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.germplasm.GermplasmNameSetting;
import org.generationcp.middleware.pojos.naming.NamingConfiguration;
import org.generationcp.middleware.service.api.GermplasmGroupNamingResult;
import org.generationcp.middleware.service.api.GermplasmGroupingService;
import org.generationcp.middleware.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service to generate Code Names (aka. Group Names)
 */
public class GermplasmCodeGenerationServiceImpl implements GermplasmCodeGenerationService {

	private static final String GERMPLASM_NOT_PART_OF_MANAGEMENT_GROUP =
			"Germplasm (gid: %s) is not part of a management group. Can not assign group name.";
	protected static final String CODING_RULE_SEQUENCE = "coding";

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

	@Override
	public Map<Integer, GermplasmGroupNamingResult> applyGroupNames(final Set<Integer> gidsToProcess,
			final NamingConfiguration namingConfiguration, final UserDefinedField nameType) throws RuleException {

		final String prefix = namingConfiguration.getPrefix();
		namingConfiguration.setSequenceCounter(this.germplasmNamingService.getNextSequence(prefix));

		final List<String> executionOrder = Arrays.asList(this.ruleFactory.getRuleSequenceForNamespace(CODING_RULE_SEQUENCE));

		final CodingRuleExecutionContext codingRuleExecutionContext = new CodingRuleExecutionContext(executionOrder, namingConfiguration);

		final Map<Integer, GermplasmGroupNamingResult> assignCodesResultsMap = new LinkedHashMap<>();

		for (final Integer gid : gidsToProcess) {
			assignCodesResultsMap.put(gid, applyGroupName(gid, namingConfiguration, nameType, codingRuleExecutionContext));
			codingRuleExecutionContext.reset();
		}

		final int lastSequenceUsed = namingConfiguration.getSequenceCounter() - 1;
		this.germplasmNamingService.saveLastSequenceUsed(prefix, lastSequenceUsed);

		return assignCodesResultsMap;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public GermplasmGroupNamingResult applyGroupName(final Integer gid, final NamingConfiguration namingConfiguration,
			final UserDefinedField nameType, final CodingRuleExecutionContext codingRuleExecutionContext) throws RuleException {

		final GermplasmGroupNamingResult result = new GermplasmGroupNamingResult();

		final Germplasm germplasm = this.germplasmDataManager.getGermplasmByGID(gid);

		if (germplasm.getMgid() == null || germplasm.getMgid() == 0) {
			result.addMessage(String.format(GERMPLASM_NOT_PART_OF_MANAGEMENT_GROUP, germplasm.getGid()));
			return result;
		}

		final List<Germplasm> groupMembers = this.germplasmGroupingService.getGroupMembers(germplasm.getMgid());
		final String generatedCodeName = (String) rulesService.runRules(codingRuleExecutionContext);

		for (final Germplasm member : groupMembers) {
			// TODO: Pass the userId and locationId. Hard coded to zero for now.
			this.addName(member, generatedCodeName, nameType, 0, 0, result);
		}

		return result;

	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public GermplasmGroupNamingResult applyGroupName(final Integer gid, final GermplasmNameSetting setting, final UserDefinedField nameType,
			final Integer userId, final Integer locationId) {

		GermplasmGroupNamingResult result = new GermplasmGroupNamingResult();

		final Germplasm germplasm = this.germplasmDataManager.getGermplasmByGID(gid);

		if (germplasm.getMgid() == null || germplasm.getMgid() == 0) {
			result.addMessage(
					String.format("Germplasm (gid: %s) is not part of a management group. Can not assign group name.", germplasm.getGid()));
			return result;
		}

		final List<Germplasm> groupMembers = this.germplasmGroupingService.getGroupMembers(germplasm.getMgid());
		final String nameWithSequence = this.germplasmNamingService.generateNextNameAndIncrementSequence(setting);

		// TODO performance tuning when processing large number of group members
		for (final Germplasm member : groupMembers) {
			this.addName(member, nameWithSequence, nameType, userId, locationId, result);
		}

		return result;
	}

	@Override
	public String getNextNameInSequence(final GermplasmNameSetting setting) throws InvalidGermplasmNameSettingException {
		return this.germplasmNamingService.getNextNameInSequence(setting);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Map<Integer, GermplasmGroupNamingResult> applyGroupNames(final Set<Integer> gids, final GermplasmNameSetting setting,
			final UserDefinedField nameType, final Integer userId, final Integer locationId) {
		final Map<Integer, GermplasmGroupNamingResult> assignCodesResultsMap = new LinkedHashMap<>();
		final boolean startNumberSpecified = setting.getStartNumber() != null;
		Integer startNumber = setting.getStartNumber();
		for (final Integer gid : gids) {
			// Increment start number of succeeding germplasm processed based on initial start # specified, if any
			if (startNumberSpecified) {
				setting.setStartNumber(startNumber++);
			}
			final GermplasmGroupNamingResult result = this.applyGroupName(gid, setting, nameType, userId, locationId);
			assignCodesResultsMap.put(gid, result);
		}
		return assignCodesResultsMap;
	}

	protected void addName(final Germplasm germplasm, final String groupName, final UserDefinedField nameType, final Integer userId,
			final Integer locationId, final GermplasmGroupNamingResult result) {

		final List<Name> currentNames = germplasm.getNames();

		Name existingNameOfGivenType = null;
		if (!currentNames.isEmpty() && nameType != null) {
			for (final Name name : currentNames) {
				if (nameType.getFldno().equals(name.getTypeId())) {
					existingNameOfGivenType = name;
					break;
				}
			}
		}

		if (existingNameOfGivenType == null) {
			// Make the current preferred name as non-preferred by setting nstat = 0
			final Name currentPreferredName = germplasm.findPreferredName();
			if (currentPreferredName != null) {
				// nstat = 0 means it is not a preferred name.
				currentPreferredName.setNstat(0);
			}

			final Name name = new Name();
			name.setGermplasmId(germplasm.getGid());
			name.setTypeId(nameType.getFldno());
			name.setNval(groupName);
			// nstat = 1 means it is preferred name.
			name.setNstat(1);
			name.setUserId(userId);
			name.setLocationId(locationId);
			name.setNdate(Util.getCurrentDateAsIntegerValue());
			name.setReferenceId(0);

			germplasm.getNames().add(name);
			this.germplasmDataManager.save(germplasm);
			result.addMessage(
					String.format("Germplasm (gid: %s) successfully assigned name %s of type %s as a preferred name.", germplasm.getGid(),
							groupName, nameType.getFcode()));
		} else {
			result.addMessage(String.format("Germplasm (gid: %s) already has existing name %s of type %s. Supplied name %s was not added.",
					germplasm.getGid(), existingNameOfGivenType.getNval(), nameType.getFcode(), groupName));
		}
	}


}
