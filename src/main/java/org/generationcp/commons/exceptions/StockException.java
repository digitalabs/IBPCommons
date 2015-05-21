package org.generationcp.commons.exceptions;

public class StockException extends Exception {
	
	private static final long serialVersionUID = 3894984850856720932L;
	
	private final String messageKey;
	private final Object[] messageParameters;
	
	public StockException() {
		this(null, new Object[]{});
	}
	
	/**
	 * Instantiates a new stock exception.
	 *
	 * @param messageKey the messageKey
	 */
	public StockException(String messageKey,Object[] messageParameters) {
		super(messageKey);
		this.messageKey = messageKey;
		this.messageParameters = messageParameters;
	}

	/**
	 * Instantiates a new stock exception.
	 *
	 * @param messageKey the messageKey
	 */
	public StockException(String messageKey) {
		super(messageKey);
		this.messageKey = messageKey;
		this.messageParameters = null;
	}

	/**
	 * Instantiates a new stock exception.
	 *
	 * @param messageKey the messageKey
	 * @param cause the cause
	 */
	public StockException(String messageKey, Throwable cause) {
		super(messageKey, cause);
		this.messageKey = messageKey;
		this.messageParameters = null;
}

	public String getMessageKey() {
		return messageKey;
	}

	public Object[] getMessageParameters() {
		return messageParameters;
	}
}
