
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

		ExternalResource tutorialLink = new ExternalResource(this.getOnlineLink(link));

		if (!propertyLink.isEmpty()) {
			event.getComponent().getWindow().open(tutorialLink, " _BLANK");
		}
	}

	String getOnlineLink(final HelpModule link) {
		return HelpDocumentUtil.getOnLineLink(this.helpProperties.getProperty(link.getPropertyName()));
	} 

	public void setHelpProperties(Properties helpProperties) {
		this.helpProperties = helpProperties;
	}

}
