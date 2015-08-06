
package org.generationcp.commons.help.document;

import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button;
import com.vaadin.ui.themes.BaseTheme;

@Configurable
public class HelpButton extends Button {

	private static final long serialVersionUID = 6058971413607232224L;

	private static final String ICON =
			"<span class='bms-fa-question-circle' style='position: relative; left: 20px; top:10px; color: #5A5A5A;font-size: 20px; font-weight: bold;'></span>";

	public HelpButton(final HelpModule link, String description) {
		super();
		this.setHtmlContentAllowed(true);
		this.setCaption(ICON);
		this.setDescription(description);
		this.setStyleName(BaseTheme.BUTTON_LINK);
		this.setWidth("50px");
		this.setHeight("25px");

		this.addListener(new HelpButtonClickListener(link));
	}

}
