
package org.generationcp.commons.help.document;

import java.net.URL;

import javax.annotation.Resource;

import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

@Configurable
public class HelpButton extends Button {

	private static final long serialVersionUID = 6058971413607232224L;

	private static final String ICON =
			"<span class='bms-fa-question-circle' style='position: relative; left: 20px; top:10px; color: #5A5A5A;font-size: 20px; font-weight: bold;'></span>";

	@Resource
	private WorkbenchDataManager workbenchDataManager;

	public HelpButton(final HELP_MODULE link, String description) {
		super();
		this.setHtmlContentAllowed(true);
		this.setCaption(ICON);
		this.setDescription(description);
		this.setStyleName(BaseTheme.BUTTON_LINK);
		this.setWidth("50px");
		this.setHeight("25px");

		this.addListener(new ClickListener() {

			private static final long serialVersionUID = 9187637357929402469L;

			@Override
			public void buttonClick(ClickEvent event) {
				boolean hasInternetConnection = HelpDocumentUtil.isIBPDomainReachable(link.getOnLineLink());

				Window currentWindow = event.getComponent().getWindow();
				ExternalResource tutorialLink = HelpButton.this.getTutorialLink(link, currentWindow, hasInternetConnection);

				String installationDirectory = HelpDocumentUtil.getInstallationDirectory(HelpButton.this.workbenchDataManager);
				if (hasInternetConnection || HelpDocumentUtil.isDocumentsFolderFound(installationDirectory)) {
					event.getComponent().getWindow().open(tutorialLink, " _BLANK");
				} else {
					HelpWindow helpWindow = new HelpWindow(HelpButton.this.workbenchDataManager);
					event.getComponent().getParent().getWindow().addWindow(helpWindow);
				}
			}

		});
	}

	private String getOfflineLink(final HELP_MODULE link, Window currentWindow) {
		URL currentURL = currentWindow.getURL();
		String host = currentURL.getHost();
		Integer port = currentURL.getPort();
		return "http://" + host + ":" + port + "/" + link.getOffLineLink();
	}

	public ExternalResource getTutorialLink(final HELP_MODULE link, Window currentWindow, boolean hasInternetConnection) {
		ExternalResource tutorialLink = null;
		if (hasInternetConnection) {
			tutorialLink = new ExternalResource(link.getOnLineLink());
		} else {
			String offlineLink = HelpButton.this.getOfflineLink(link, currentWindow);
			tutorialLink = new ExternalResource(offlineLink);
		}
		return tutorialLink;
	}
}
