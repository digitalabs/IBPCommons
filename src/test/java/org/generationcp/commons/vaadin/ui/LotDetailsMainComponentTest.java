package org.generationcp.commons.vaadin.ui;

import java.util.List;

import org.generationcp.middleware.data.initializer.ListInventoryDataInitializer;
import org.generationcp.middleware.domain.inventory.LotDetails;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LotDetailsMainComponentTest {

	@Mock
	private InventoryDataManager inventoryDataManager;

	@InjectMocks
	private LotDetailsMainComponent lotDetailsMainComponentForInventoryView = new LotDetailsMainComponent(1, 1);

	@InjectMocks
	private LotDetailsMainComponent lotDetailsMainComponentForListView = new LotDetailsMainComponent(1, null);

	@Before
	public void setUp() {
		List<? extends LotDetails> inventoryDetails = ListInventoryDataInitializer.createLotDetails(1);
		Mockito.when(this.inventoryDataManager.getLotDetailsForGermplasm(Mockito.anyInt()))
				.thenReturn((List<LotDetails>) inventoryDetails);
	}

	@Test
	public void testInitializeValuesForInventoryView() {
		lotDetailsMainComponentForInventoryView.initializeValues();
		Assert.assertEquals(1, lotDetailsMainComponentForInventoryView.getLotDetails().size());
	}

	@Test
	public void testInitializeValuesForListView() {
		lotDetailsMainComponentForListView.initializeValues();
		Assert.assertEquals(5, lotDetailsMainComponentForListView.getLotDetails().size());
	}

	@Test
	public void testLayoutComponentsForInventoryView() {
		lotDetailsMainComponentForInventoryView.initializeValues();
		lotDetailsMainComponentForInventoryView.layoutComponents();
		Assert.assertEquals(1, lotDetailsMainComponentForInventoryView.getComponentCount());
	}

	@Test
	public void testLayoutComponentsForListView() {
		lotDetailsMainComponentForListView.initializeValues();
		lotDetailsMainComponentForListView.layoutComponents();
		Assert.assertEquals(5, lotDetailsMainComponentForListView.getComponentCount());
	}

}
