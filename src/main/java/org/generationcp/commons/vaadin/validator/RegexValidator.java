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
