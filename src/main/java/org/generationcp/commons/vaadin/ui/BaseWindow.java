package org.generationcp.commons.vaadin.ui;

import com.vaadin.ui.Window;

public class BaseWindow extends Window {
	
	private static final long serialVersionUID = -1982854106348538401L;
	
	public BaseWindow(String windowName){
		super(windowName);
	}

	@Override
	public void addWindow(Window window) throws IllegalArgumentException,
			NullPointerException {
		super.addWindow(window);
		window.focus();
	}

}