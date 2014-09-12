package org.generationcp.commons.vaadin.ui;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class BaseSubWindow extends Window implements InitializingBean, Handler {
	
	private static final long serialVersionUID = 355237906901597933L;
	private Action closeAction = new ShortcutAction("ESCAPE", 
			ShortcutAction.KeyCode.ESCAPE, null);

    private boolean overrideFocus = false;
    private Window parentWindow;

	public BaseSubWindow() {
        super();
    	this.initializeWindow();

    }
	
	public BaseSubWindow(String windowName){
		super(windowName);
        this.initializeWindow();
    }

	@Override
	public Action[] getActions(Object target, Object sender) {
		return new Action[]{closeAction};
	}

	@Override
	public void handleAction(Action action, Object sender, Object target) {
		if (action == closeAction){
            // focus to previous window (for nested windows)
            if (parentWindow != null) {
                parentWindow.focus();
            }

    		closeWindow();
		} 
		
	}

	protected void closeWindow() {
		this.getParent().removeWindow(this);
	}
	
	protected void initializeWindow() {
		this.addStyleName(Reindeer.WINDOW_LIGHT);
		this.addActionHandler(this);
        this.setModal(true);
	}

    public void setOverrideFocus(boolean val) {
        overrideFocus = val;
    }

    public void setParentWindow(Window parentWindow) {
        this.parentWindow = parentWindow;
    }

    @Override
    public void attach() {
        if (!overrideFocus) {
            this.focus();
        }

        super.attach();
    }


    @Override
    public void afterPropertiesSet() throws Exception {
    }
}

