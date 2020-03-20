
package org.generationcp.commons.parsing.validation;

import java.util.Map;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 2/26/2015 Time: 5:18 PM
 */
public abstract class ParsingValidator {

	public static final String PAIRED_COLUMN_VALUE_KEY = "PAIRED_COLUMN_VALUE";

	private String message;
	private final boolean skipIfEmpty;
	private Integer pairedColumnIndex;

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

	public Integer getPairedColumnIndex() {
		return this.pairedColumnIndex;
	}

	public void setPairedColumnIndex(final Integer pairedColumnIndex) {
		this.pairedColumnIndex = pairedColumnIndex;
	}
}
