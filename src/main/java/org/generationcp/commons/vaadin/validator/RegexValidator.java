/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.commons.vaadin.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vaadin.data.validator.AbstractValidator;

/**
 * A validator for Vaadin input fields.<br>
 * This validator treats input as {@link String} and performs regular expression
 * based validation.
 * 
 * @author Glenn Marintes
 */
public class RegexValidator extends AbstractValidator {
    private static final long serialVersionUID = 1L;
    
    private Pattern pattern;
    private boolean match;

    public RegexValidator(String errorMessage, String pattern, boolean match) {
        super(errorMessage);
        
        this.pattern = Pattern.compile(pattern);
        this.match = match;
    }
    
    public RegexValidator(String errorMessage, Pattern pattern, boolean match) {
        super(errorMessage);
        
        this.pattern = pattern;
        this.match = match;
    }

    @Override
    public boolean isValid(Object value) {
        if (!String.class.isInstance(value)) {
            return true;
        }
        
        Matcher matcher = pattern.matcher(value.toString());
        
        return match ? matcher.matches() : !matcher.matches();
    }

}
