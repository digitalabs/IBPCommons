
package org.generationcp.commons.help.document;

import java.util.Properties;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;

@Configurable
public class HelpButtonClickListener implements Button.ClickListener {

	private static final long serialVersionUID = -7357700787477929587L;

	@Resource
	private Properties helpProperties;

	private HelpModule link;

	public HelpButtonClickListener(HelpModule link) {
		this.link = link;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		String propertyLink = this.helpProperties.getProperty(this.link.getPropertyName());

		boolean hasInternetConnection = HelpDocumentUtil.isIBPDomainReachable(this.getOnlineLink(this.link));
		Window currentWindow = event.getComponent().getWindow();
		ExternalResource tutorialLink = this.getTutorialLink(this.link, currentWindow, hasInternetConnection);

		if (!propertyLink.isEmpty() && hasInternetConnection) {
			event.getComponent().getWindow().open(tutorialLink, " _BLANK");
		} else if (!hasInternetConnection) {
			HelpWindow helpWindow = new HelpWindow();
			event.getComponent().getParent().getWindow().addWindow(helpWindow);
		}
	}

	String getOnlineLink(final HelpModule link) {
		return HelpDocumentUtil.getOnLineLink(this.helpProperties.getProperty(link.getPropertyName()));
	}

	ExternalResource getTutorialLink(final HelpModule link, Window currentWindow, boolean hasInternetConnection) {
		ExternalResource tutorialLink = null;
		if (hasInternetConnection) {
			tutorialLink = new ExternalResource(this.getOnlineLink(link));
		} 
		return tutorialLink;
	}

	public void setHelpProperties(Properties helpProperties) {
		this.helpProperties = helpProperties;
	}

}
