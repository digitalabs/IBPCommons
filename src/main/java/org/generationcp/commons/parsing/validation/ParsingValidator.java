
package org.generationcp.commons.parsing.validation;

import java.util.Map;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 2/26/2015 Time: 5:18 PM
 */
public abstract class ParsingValidator {

	private String message;
	private final boolean skipIfEmpty;

	public ParsingValidator(boolean skipIfEmpty) {
		this.skipIfEmpty = skipIfEmpty;
	}

	public abstract boolean isParsedValueValid(String value, Map<String, Object> additionalParams);

	public void setValidationErrorMessage(String message) {
		this.message = message;
	}

	public String getValidationErrorMessage() {
		return this.message;
	}

	public boolean isSkipIfEmpty() {
		return this.skipIfEmpty;
	}
}
