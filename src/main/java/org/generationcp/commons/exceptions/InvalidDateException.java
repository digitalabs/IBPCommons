
package org.generationcp.commons.exceptions;

import org.generationcp.commons.constant.CommonMessage;

public class InvalidDateException extends Exception {

	private static final long serialVersionUID = 1L;
	private static CommonMessage messageCode;

	public InvalidDateException(String message, CommonMessage messageCode) {
		super(message);
		InvalidDateException.setMessageCode(messageCode);
	}

	public InvalidDateException(String message, Throwable cause, CommonMessage messageCode) {
		super(message, cause);
		InvalidDateException.setMessageCode(messageCode);
	}

	public static CommonMessage getMessageCode() {
		return InvalidDateException.messageCode;
	}

	public static void setMessageCode(CommonMessage messageCode) {
		InvalidDateException.messageCode = messageCode;
	}
}
