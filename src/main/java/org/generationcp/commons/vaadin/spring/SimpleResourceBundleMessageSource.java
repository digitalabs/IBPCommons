/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.commons.vaadin.spring;

import java.io.Serializable;
import java.util.Locale;

import org.springframework.context.support.ResourceBundleMessageSource;

import com.vaadin.data.Property;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;

public class SimpleResourceBundleMessageSource extends ResourceBundleMessageSource implements Serializable {

	private static final long serialVersionUID = 1L;

	private Locale locale;

	public Locale getLocale() {
		return this.locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getMessage(String code) {
		return super.getMessage(code, null, this.locale);
	}

	public String getMessage(Enum<?> code) {
		return super.getMessage(code.name(), null, this.locale);
	}

	public String getMessage(String code, Object... args) {
		return super.getMessage(code, args, this.locale);
	}

	public String getMessage(Enum<?> code, Object... args) {
		return super.getMessage(code.name(), args, this.locale);
	}

	public void setValue(Property component, String messageCode) {
		component.setValue(this.getMessage(messageCode));
	}

	public void setValue(Property component, Enum<?> messageCode) {
		component.setValue(this.getMessage(messageCode.name()));
	}

	public void setValue(Property component, Enum<?> messageCode, Object... args) {
		component.setValue(this.getMessage(messageCode.name(), args));
	}

	public void setCaption(Component component, String messageCode) {
		component.setCaption(this.getMessage(messageCode));
	}

	public void setCaption(Component component, Enum<?> messageCode) {
		component.setCaption(this.getMessage(messageCode.name()));
	}

	public void setDescription(AbstractComponent abstractComponent, String messageCode) {
		abstractComponent.setDescription(this.getMessage(messageCode));
	}

	public void setDescription(AbstractComponent abstractComponent, Enum<?> messageCode) {
		abstractComponent.setDescription(this.getMessage(messageCode.name()));
	}

	public void setColumnHeader(Table table, String propertyId, String messageCode) {
		table.setColumnHeader(propertyId, this.getMessage(messageCode));
	}

	public void setColumnHeader(Table table, String propertyId, Enum<?> messageCode) {
		table.setColumnHeader(propertyId, this.getMessage(messageCode.name()));
	}

}
