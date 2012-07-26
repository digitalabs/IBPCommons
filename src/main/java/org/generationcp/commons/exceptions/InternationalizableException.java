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

import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Class InternationalizableException.
 * 
 * 
 * @author Joyce Avestro
 * 
 * 
 */
@Configurable
public class InternationalizableException extends RuntimeException implements InitializingBean, InternationalizableComponent{

    private static final long serialVersionUID = 1L;
    
    private String caption;
    
    private String description;

    /** The internationalization code for caption. */
    private Enum<?> i18nCaption;
    
    /** The internationalization code for description. */
    private Enum<?> i18nDescription;

    /** The message source. */
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    /**
     * Instantiates a new InternationalizableException.
     */
    public InternationalizableException() {
    }

    /**
     * Instantiates a new InternationalizableException.
     *
     * @param e a Throwable object
     */
    public InternationalizableException(Throwable e) {
        super(e);
    }

    /**
     * Instantiates a new InternationalizableException
     *
     * @param e a Throwable object
     * @param i18nCaption the internationalization code for caption
     * @param i18nDescription the internationalization code for description
     */
    public InternationalizableException(Throwable e, Enum<?> i18nCaption, Enum<?> i18nDescription) {
        super(e);
        this.i18nCaption = i18nCaption;
        this.i18nDescription = i18nDescription;
    }

    /**
     * Gets the caption.
     *
     * @return the caption
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Sets the caption.
     *
     * @param caption the new caption
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * Sets the caption using the internationalization code.
     *
     * @param code the new caption
     */
    public void setCaption(Enum<?> code) {
        this.caption = messageSource.getMessage(code);
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        String toReturn = null;
        if (description != null && !description.equals("")) {
            toReturn = "</br>" + description;
        } 
        return toReturn;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the description using the internationalization code.
     *
     * @param code the new description
     */
    public void setDescription(Enum<?> code) {
        this.description = messageSource.getMessage(code);
    }

    /* (non-Javadoc)
     * @see org.generationcp.commons.vaadin.spring.InternationalizableComponent#updateLabels()
     */
    @Override
    public void updateLabels() {
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {         
        if (i18nCaption != null) {
            this.caption = messageSource.getMessage(i18nCaption);
        }
        if (i18nDescription != null){
            this.description = messageSource.getMessage(i18nDescription);
        }
    }

}
