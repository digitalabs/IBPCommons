
package org.generationcp.commons.util;

import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class InternationalizableException extends RuntimeException implements InitializingBean, InternationalizableComponent{

    private static final long serialVersionUID = 1L;
    private String caption;
    private String description;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public InternationalizableException() {
    }

    public InternationalizableException(Throwable e) {
        this(e.getMessage());
        super.setStackTrace(e.getStackTrace());
    }

    public InternationalizableException(String message) {
        super(message);
        this.caption = null;
        this.description = null;
    }

    public InternationalizableException(Throwable e, String caption, String description) {
        super(e.getMessage());
        super.setStackTrace(e.getStackTrace());
        this.caption = caption;
        this.description = description;
    }

    public InternationalizableException(String message, String caption, String description) {
        super(message);
        this.caption = caption;
        this.description = description;
    }

    public InternationalizableException(String message, Enum<?> i18nCaption, Enum<?> i18nDescription) {
        super(message);
        this.setCaption(i18nCaption);
        this.setDescription(i18nDescription);
    }

    public InternationalizableException(Throwable e, Enum<?> i18nCaption, Enum<?> description) {
        super(e.getMessage());
        super.setStackTrace(e.getStackTrace());
        this.setCaption(i18nCaption);
        this.setDescription(description);
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setCaption(Enum<?> code) {
        System.out.println("messageSource:" + messageSource);
        if (messageSource != null) {
            this.caption = messageSource.getMessage(code);
        } else {
            this.description = code.toString();
        }

    }

    public String getDescription() {
        String toReturn = null;
        if (description != null && !description.equals("")) {
            toReturn = "</br>" + description;
        } else if (getLocalizedMessage() != null) {
            toReturn = "</br>" + getLocalizedMessage();
        }
        return toReturn;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDescription(Enum<?> code) {
        System.out.println("messageSource:" + messageSource);
        if (messageSource != null) {
            this.description = messageSource.getMessage(code);
        } else {
            this.description = code.toString();
        }
    }

    @Override
    public String getLocalizedMessage() {
        // TODO: Internationalize
        return super.getLocalizedMessage();
    }

    @Override
    public void updateLabels() {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }

}
