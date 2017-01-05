package org.generationcp.commons.Listener;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

import org.generationcp.commons.vaadin.ui.LotDetailsMainComponent;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class LotDetailsButtonClickListener implements Button.ClickListener {

	private final Integer gid;
	private final String germplasmName;
	private final Component source;
	private final Integer lotId;

	private static final String LOT_DETAIL_POPUP_HEADER_TITLE = "Lot Details for ";

	public LotDetailsButtonClickListener(Integer gid, String germplasmName, Component source, Integer lotId) {
		super();
		this.gid = gid;
		this.germplasmName = germplasmName;
		this.source = source;
		this.lotId = lotId;
	}

	@Override
	public void buttonClick(Button.ClickEvent event) {
		LotDetailsMainComponent lotDetailsMainComponent = new LotDetailsMainComponent(this.gid, this.lotId);

		Window lotDetailsMainWindow = new BaseSubWindow(
				LotDetailsButtonClickListener.LOT_DETAIL_POPUP_HEADER_TITLE + this.germplasmName + " (GID:" + this.gid + ")");
		lotDetailsMainWindow.setWidth("810px");
		lotDetailsMainWindow.setHeight("350px");
		lotDetailsMainWindow.addComponent(lotDetailsMainComponent);
		this.source.getWindow().addWindow(lotDetailsMainWindow);
		lotDetailsMainWindow.center();
	}
}
