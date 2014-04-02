package org.generationcp.commons.vaadin.ui;

import org.generationcp.commons.vaadin.theme.Bootstrap;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class ComponentTree extends CssLayout {

	private static final long serialVersionUID = 740158017459466809L;
	
	public ComponentTree () {
		super();
	}
	
	public ComponentTreeItem addChild (Component c) {
		ComponentTreeItem i = new ComponentTreeItem(c);
		addComponent(i);
		return i;
	}
	
}
