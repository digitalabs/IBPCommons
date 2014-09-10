package org.generationcp.commons.vaadin.ui;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

public class BaseSubWindow extends Window implements Handler {
	
	private static final long serialVersionUID = 355237906901597933L;
	private Action closeAction = new ShortcutAction("ESCAPE", 
			ShortcutAction.KeyCode.ESCAPE, null);
	

	public BaseSubWindow() {
		super();
		initializeWindow();
	}
	
	public BaseSubWindow(String windowName){
		super(windowName);
	}

	@Override
	public Action[] getActions(Object target, Object sender) {
		return new Action[]{closeAction};
	}

	@Override
	public void handleAction(Action action, Object sender, Object target) {
		if (action == closeAction){
    		closeWindow();
		} 
		
	}

	protected void closeWindow() {
		this.getParent().removeWindow(this);
	}
	
	protected void initializeWindow() {
		addStyleName(Reindeer.WINDOW_LIGHT);
		addActionHandler(this);
	}


}

