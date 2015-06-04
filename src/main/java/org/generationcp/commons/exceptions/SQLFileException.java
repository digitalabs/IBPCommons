
package org.generationcp.commons.exceptions;

public class SQLFileException extends Exception {

	private static final long serialVersionUID = 1949011533841191893L;

	public SQLFileException(Throwable cause) {
		super("Error executing SQL file.", cause);
	}

	public SQLFileException(String message, Throwable cause) {
		super(message, cause);
	}

}
