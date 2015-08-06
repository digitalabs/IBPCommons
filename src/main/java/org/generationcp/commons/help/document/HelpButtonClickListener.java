
package org.generationcp.commons.help.document;

import java.net.URL;
import java.util.Properties;

import javax.annotation.Resource;

import org.generationcp.commons.tomcat.util.TomcatUtil;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;

@Configurable
public class HelpButtonClickListener implements Button.ClickListener {

	private static final long serialVersionUID = -7357700787477929587L;

	@Resource
	private TomcatUtil tomcatUtil;
	@Resource
	private WorkbenchDataManager workbenchDataManager;
	@Resource
	private Properties helpProperties;

	private HelpModule link;

	public HelpButtonClickListener(HelpModule link) {
		this.link = link;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		boolean hasInternetConnection = HelpDocumentUtil.isIBPDomainReachable(this.getOnlineLink(this.link));

		Window currentWindow = event.getComponent().getWindow();
		ExternalResource tutorialLink = this.getTutorialLink(this.link, currentWindow, hasInternetConnection);

		String installationDirectory = HelpDocumentUtil.getInstallationDirectory(this.workbenchDataManager);
		if (hasInternetConnection || HelpDocumentUtil.isDocumentsFolderFound(installationDirectory)) {
			event.getComponent().getWindow().open(tutorialLink, " _BLANK");
		} else {
			HelpWindow helpWindow = new HelpWindow(this.workbenchDataManager, this.tomcatUtil);
			event.getComponent().getParent().getWindow().addWindow(helpWindow);
		}
	}

	String getOnlineLink(final HelpModule link) {
		return HelpDocumentUtil.getOnLineLink(this.helpProperties.getProperty(link.getPropertyName()));
	}

	String getOfflineLink(final HelpModule link, URL currentUrl) {
		return HelpDocumentUtil.getOffLineLink(currentUrl, this.helpProperties.getProperty(link.getPropertyName()));
	}

	ExternalResource getTutorialLink(final HelpModule link, Window currentWindow, boolean hasInternetConnection) {
		ExternalResource tutorialLink = null;
		if (hasInternetConnection) {
			tutorialLink = new ExternalResource(this.getOnlineLink(link));
		} else {
			String offlineLink = this.getOfflineLink(link, currentWindow.getURL());
			tutorialLink = new ExternalResource(offlineLink);
		}
		return tutorialLink;
	}

	public void setTomcatUtil(TomcatUtil tomcatUtil) {
		this.tomcatUtil = tomcatUtil;
	}

	public void setWorkbenchDataManager(WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}

	public void setHelpProperties(Properties helpProperties) {
		this.helpProperties = helpProperties;
	}

}
