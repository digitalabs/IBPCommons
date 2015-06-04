
package org.generationcp.commons.vaadin.ui;

import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;

public class ComponentTree extends CssLayout {

	private static final long serialVersionUID = 740158017459466809L;

	public ComponentTree() {
		super();
	}

	public ComponentTreeItem addChild(Component c) {
		ComponentTreeItem i = new ComponentTreeItem(c);
		this.addComponent(i);
		return i;
	}

}
