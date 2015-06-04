/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.commons.vaadin.actions;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSourceListener;

import com.vaadin.Application;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Window;

public class UpdateComponentLabelsAction implements Serializable, SimpleResourceBundleMessageSourceListener {

	private static final long serialVersionUID = 1L;
	private final Application application;

	public UpdateComponentLabelsAction(Application application) {
		this.application = application;
	}

	/**
	 * Calls <code>updateLabels</code> of all registered windows and components.
	 */
	@Override
	public void localeChanged(Locale oldLocale, Locale newLocale) {
		Collection<Window> windows = this.application.getWindows();

		for (Window window : windows) {
			// update the labels on the window
			if (window instanceof InternationalizableComponent) {
				((InternationalizableComponent) window).updateLabels();
			}

			// update the labels on the child components
			Queue<Component> childComponents = new LinkedList<Component>();
			while (window.getComponentIterator().hasNext()) {
				Component component = window.getComponentIterator().next();
				childComponents.add(component);
			}

			while (!childComponents.isEmpty()) {
				Component component = childComponents.poll();
				if (component == null) {
					break;
				}

				if (component instanceof InternationalizableComponent) {
					((InternationalizableComponent) component).updateLabels();
				}

				if (component instanceof ComponentContainer) {
					ComponentContainer container = (ComponentContainer) component;
					while (container.getComponentIterator().hasNext()) {
						childComponents.add(window.getComponentIterator().next());
					}
				}
			}
		}
	}
}
