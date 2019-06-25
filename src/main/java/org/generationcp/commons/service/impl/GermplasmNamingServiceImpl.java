package org.generationcp.commons.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.service.GermplasmNamingService;
import org.generationcp.middleware.exceptions.InvalidGermplasmNameSettingException;
import org.generationcp.middleware.pojos.germplasm.GermplasmNameSetting;
import org.generationcp.middleware.service.api.KeySequenceRegisterService;
import org.springframework.beans.factory.annotation.Autowired;

public class GermplasmNamingServiceImpl implements GermplasmNamingService {

	@Autowired
	private KeySequenceRegisterService keySequenceRegisterService;

	@Override
	public String getNextNameInSequence(final GermplasmNameSetting setting) throws InvalidGermplasmNameSettingException {
		Integer nextNumberInSequence = this.getNextNumberInSequence(setting);

		final Integer optionalStartNumber = setting.getStartNumber();

		if (optionalStartNumber != null && optionalStartNumber > 0 && nextNumberInSequence > optionalStartNumber) {
			final String nextName = this.buildDesignationNameInSequence(nextNumberInSequence, setting);
			final String invalidStatingNumberErrorMessage =
				"Starting sequence number should be higher than or equal to next name in the sequence: " + nextName + ".";
			throw new InvalidGermplasmNameSettingException(invalidStatingNumberErrorMessage);
		}

		if (optionalStartNumber != null && nextNumberInSequence < optionalStartNumber) {
			nextNumberInSequence = optionalStartNumber;
		}

		return this.buildDesignationNameInSequence(nextNumberInSequence, setting);
	}

	int getNextNumberInSequence(final GermplasmNameSetting setting) {

		final String lastPrefixUsed = this.buildPrefixString(setting).toUpperCase();

		if (!lastPrefixUsed.isEmpty()) {
			return this.keySequenceRegisterService.getNextSequence(lastPrefixUsed);
		}

		return 1;
	}

	String buildDesignationNameInSequence(final Integer number, final GermplasmNameSetting setting) {
		final StringBuilder sb = new StringBuilder();
		sb.append(this.buildPrefixString(setting));
		sb.append(this.getNumberWithLeadingZeroesAsString(number, setting));

		if (!StringUtils.isEmpty(setting.getSuffix())) {
			sb.append(this.buildSuffixString(setting));
		}
		return sb.toString();
	}

	String buildPrefixString(final GermplasmNameSetting setting) {
		final String prefix = !StringUtils.isEmpty(setting.getPrefix()) ? setting.getPrefix().trim() : "";
		if (setting.isAddSpaceBetweenPrefixAndCode()) {
			return prefix + " ";
		}
		return prefix;
	}

	String buildSuffixString(final GermplasmNameSetting setting) {
		final String suffix = setting.getSuffix();
		if (suffix != null) {
			if (setting.isAddSpaceBetweenSuffixAndCode()) {
				return " " + suffix.trim();
			}
			return suffix.trim();
		}
		return "";
	}

	String getNumberWithLeadingZeroesAsString(final Integer number, final GermplasmNameSetting setting) {
		final Integer numOfDigits = setting.getNumOfDigits();

		if (numOfDigits == null || numOfDigits <= 0) {
			return number.toString();
		}
		return String.format("%0" + numOfDigits + "d", number);
	}

	@Override
	public String generateNextNameAndIncrementSequence(final GermplasmNameSetting setting) {
		Integer nextNumberInSequence = this.getNextNumberInSequence(setting);
		final Integer optionalStartNumber = setting.getStartNumber();
		if (optionalStartNumber != null && nextNumberInSequence < optionalStartNumber) {
			nextNumberInSequence = optionalStartNumber;
		}
		this.keySequenceRegisterService
			.saveLastSequenceUsed(this.buildPrefixString(setting).toUpperCase(), nextNumberInSequence);

		return this.buildDesignationNameInSequence(nextNumberInSequence, setting);
	}

	@Override
	public int getNextSequence(final String keyPrefix) {
		return this.keySequenceRegisterService.getNextSequence(keyPrefix);
	}

	@Override
	public void saveLastSequenceUsed(final String keyPrefix, final Integer lastSequenceUsed) {
		this.keySequenceRegisterService.saveLastSequenceUsed(keyPrefix, lastSequenceUsed);
	}
}
