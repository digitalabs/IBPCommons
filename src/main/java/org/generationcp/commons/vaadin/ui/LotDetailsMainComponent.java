package org.generationcp.commons.vaadin.ui;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.middleware.domain.inventory.LotDetails;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.VerticalLayout;

@Configurable
public class LotDetailsMainComponent extends VerticalLayout implements InitializingBean, InternationalizableComponent {

	@Autowired
	private InventoryDataManager inventoryDataManager;

	private final Integer gid;
	private final Integer lotId;
	private List<LotDetails> lotDetails;

	public LotDetailsMainComponent(Integer gid, Integer lotId) {
		this.gid = gid;
		this.lotId = lotId;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.initializeValues();
		this.layoutComponents();
	}

	public void initializeValues() {
		List<LotDetails> lotDetailsForGermplasm = this.inventoryDataManager.getLotDetailsForGermplasm(this.gid);
		this.lotDetails = new ArrayList<>();
		if (this.lotId != null) {
			for (LotDetails lot : lotDetailsForGermplasm) {
				if (lot.getLotId().equals(this.lotId)) {
					this.lotDetails.add(lot);
					break;
				}
			}
		} else {
			this.lotDetails = lotDetailsForGermplasm;
		}
	}

	public void layoutComponents() {
		for (LotDetails lot : this.lotDetails) {
			this.addComponent(new LotDetailsViewComponent(lot));
		}
	}

	@Override
	public void updateLabels() {

	}
}
