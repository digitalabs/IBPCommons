
package org.generationcp.commons.parsing.validation;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.generationcp.commons.util.DateUtil;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 2/26/2015 Time: 5:27 PM
 */
public class ValueTypeValidator extends ParsingValidator {

	private final Class expectedClass;

	public static final String GENERIC_INVALID_TYPE_MESSAGE = "common.parse.validation.error.invalid.type";

	public ValueTypeValidator(Class expectedClass) {
		this(expectedClass, true);
	}

	public ValueTypeValidator(Class expectedClass, boolean skipIfEmpty) {
		super(skipIfEmpty);
		this.expectedClass = expectedClass;
		this.setValidationErrorMessage(ValueTypeValidator.GENERIC_INVALID_TYPE_MESSAGE);
	}

	@Override
	public boolean isParsedValueValid(String value, Map<String, Object> additionalParams) {

		if (StringUtils.isEmpty(value) && this.isSkipIfEmpty()) {
			return true;
		}

		if (this.expectedClass.isAssignableFrom(Double.class)) {
			try {
				Double.parseDouble(value);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		} else if (this.expectedClass.isAssignableFrom(Date.class)) {
			return DateUtil.isValidFieldbookDate(value);
		} else if (this.expectedClass.isAssignableFrom(Integer.class)) {
			try {
				Integer.parseInt(value);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		} else {
			throw new UnsupportedOperationException();
		}
	}
}
