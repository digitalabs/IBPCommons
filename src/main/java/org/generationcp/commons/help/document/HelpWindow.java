
package org.generationcp.commons.help.document;

import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

@Configurable
public class HelpWindow extends BaseSubWindow implements InitializingBean, InternationalizableComponent {

	private static final long serialVersionUID = -5526578750776397878L;

	// Components
	private ComponentContainer rootLayout;

	private static final String WINDOW_WIDTH = "640px";
	private static final String WINDOW_HEIGHT = "415px";

	@Value("${workbench.version}")
	private String workbenchVersion;

	public HelpWindow() {
		super();
	}

	@Override
	public void afterPropertiesSet() {
		this.initializeLayout();
	}

	protected void initializeLayout() {
		this.setWidth(HelpWindow.WINDOW_WIDTH);
		this.setHeight(HelpWindow.WINDOW_HEIGHT);
		this.setResizable(false);
		this.setModal(true);
		this.setCaption("BREEDING MANAGEMENT SYSTEM | WORKBENCH");
		this.setStyleName("gcp-help-window");

		this.rootLayout = this.getContent();

		Label version = new Label(workbenchVersion);
		version.setStyleName("gcp-version");
		this.rootLayout.addComponent(version);

		Panel panel = new Panel();
		// fix for IE
		panel.setWidth("600px");
		this.rootLayout.addComponent(panel);

		CustomLayout helpLayout = new CustomLayout("help_not_installed");
		panel.setContent(helpLayout);
		return;
	}

	@Override
	public void updateLabels() {
		// no labels to update
	}

}
