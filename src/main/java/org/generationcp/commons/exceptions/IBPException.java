/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.commons.exceptions;


/**
 * <b>Description</b>: Wrapper Exception class for handling showing of error messages and notifications.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Michael Blancaflor
 * <br>
 * <b>File Created</b>: Jul 26, 2012
 */
public class IBPException extends RuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8259974974250542079L;

    /** The error caption. */
    private String errorCaption;
    
    /** The error desc. */
    private String errorDesc;
    
    /**
     * Instantiates a new iBP exception.
     *
     * @param errorCaption the error caption
     * @param errorDesc the error desc
     * @param t the t
     */
    public IBPException(String errorCaption, String errorDesc, Throwable t) {
        super(t);
        this.errorCaption = errorCaption;
        this.errorDesc = errorDesc;
    }
    
    /**
     * Gets the error caption.
     *
     * @return the error caption
     */
    public String getErrorCaption() {
        return errorCaption;
    }

    /**
     * Sets the error caption.
     *
     * @param errorCaption the new error caption
     */
    public void setErrorCaption(String errorCaption) {
        this.errorCaption = errorCaption;
    }

    /**
     * Gets the error desc.
     *
     * @return the error desc
     */
    public String getErrorDesc() {
        return errorDesc;
    }

    /**
     * Sets the error desc.
     *
     * @param errorDesc the new error desc
     */
    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }
}
