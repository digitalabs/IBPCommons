
package org.generationcp.commons.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.service.CrossNameService;
import org.generationcp.commons.settings.CrossNameSetting;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Utility class for generating next cross name / number given CrossNameSetting object
 *
 * @author Darla Ani
 *
 */
@Configurable
public class CrossNameServiceImpl implements CrossNameService {

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	private Integer nextNumberInSequence = 1;

	/**
	 * Returns the generated next name in sequence with the given CrossNameSetting parameters
	 *
	 * @param setting
	 * @return
	 * @throws MiddlewareQueryException
	 */
	@Override
	public String getNextNameInSequence(CrossNameSetting setting) throws MiddlewareQueryException {
		this.nextNumberInSequence = this.getNextNumberInSequence(setting);

		return this.buildNextNameInSequence(this.nextNumberInSequence, setting);
	}

	/**
	 * Returns the generated next number in sequence with the given CrossNameSetting parameters
	 *
	 * @param setting
	 * @return
	 * @throws MiddlewareQueryException
	 */
	@Override
	public Integer getNextNumberInSequence(CrossNameSetting setting) throws MiddlewareQueryException {
		String lastPrefixUsed = this.buildPrefixString(setting);
		this.nextNumberInSequence = 1;

		Integer startNumber = setting.getStartNumber();
		if (startNumber != null && startNumber > 0) {
			this.nextNumberInSequence = startNumber;
		} else {
			String nextSequenceNumberString =
					this.germplasmDataManager.getNextSequenceNumberForCrossName(lastPrefixUsed.toUpperCase().trim());
			this.nextNumberInSequence = Integer.parseInt(nextSequenceNumberString);
		}

		return this.nextNumberInSequence;

	}

	String buildPrefixString(CrossNameSetting setting) {
		String prefix = setting.getPrefix().trim();
		if (setting.isAddSpaceBetweenPrefixAndCode()) {
			return prefix + " ";
		}
		return prefix;
	}

	String buildSuffixString(CrossNameSetting setting) {
		String suffix = setting.getSuffix().trim();
		if (setting.isAddSpaceBetweenSuffixAndCode()) {
			return " " + suffix;
		}
		return suffix;
	}

	@Override
	public String buildNextNameInSequence(Integer number, CrossNameSetting setting) {
		StringBuilder sb = new StringBuilder();
		sb.append(this.buildPrefixString(setting));
		sb.append(this.getNumberWithLeadingZeroesAsString(number, setting));
		if (!StringUtils.isEmpty(setting.getSuffix())) {
			sb.append(this.buildSuffixString(setting));
		}
		return sb.toString();
	}

	String getNumberWithLeadingZeroesAsString(Integer number, CrossNameSetting setting) {
		StringBuilder sb = new StringBuilder();
		String numberString = number.toString();
		Integer numOfDigits = setting.getNumOfDigits();

		if (numOfDigits != null && numOfDigits > 0) {
			int numOfZerosNeeded = numOfDigits - numberString.length();
			if (numOfZerosNeeded > 0) {
				for (int i = 0; i < numOfZerosNeeded; i++) {
					sb.append("0");
				}
			}

		}
		sb.append(number);
		return sb.toString();
	}
}
