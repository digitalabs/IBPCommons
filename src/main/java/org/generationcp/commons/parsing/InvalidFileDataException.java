
package org.generationcp.commons.parsing;

/**
 * When imported file have empty observation sheet then this exception will be used.
 *
 */
public class InvalidFileDataException extends Exception {

	private static final long serialVersionUID = -1094438052651480748L;
	private final String message;
	private final int errorRowIndex;
	private final String errorValue;
	private final String errorColumn;

	public InvalidFileDataException() {
		this(null, 0, null, null);
	}

	public InvalidFileDataException(String message) {
		this(message, 0, null, null);
	}

	public InvalidFileDataException(String message, int errorRowIndex, String errorValue, String errorColumn) {
		this.message = message;
		this.errorRowIndex = errorRowIndex;
		this.errorValue = errorValue;
		this.errorColumn = errorColumn;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	public Object[] getMessageParameters() {
		return new Object[] {this.errorRowIndex, this.errorColumn, this.errorValue};
	}
}
