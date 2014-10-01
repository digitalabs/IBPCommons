package org.generationcp.commons.exceptions;

import org.generationcp.commons.constant.VaadinMessage;

public class InvalidDateException extends Exception {

    private static final long serialVersionUID = 1L;
    private static VaadinMessage messageCode;
    
    public InvalidDateException(String message, VaadinMessage messageCode) {
        super(message);
        InvalidDateException.setMessageCode(messageCode);
    }
    
    public InvalidDateException(String message, Throwable cause, VaadinMessage messageCode) {
        super(message, cause);
        InvalidDateException.setMessageCode(messageCode);
    }

	public static VaadinMessage getMessageCode() {
		return messageCode;
	}

	public static void setMessageCode(VaadinMessage messageCode) {
		InvalidDateException.messageCode = messageCode;
	}
}
