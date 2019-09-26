package org.generationcp.commons.vaadin.ui;


import com.vaadin.data.Item;
import com.vaadin.ui.Table;
import org.generationcp.middleware.data.initializer.InventoryDetailsTestDataInitializer;
import org.generationcp.middleware.data.initializer.ListInventoryDataInitializer;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collection;

@RunWith(MockitoJUnitRunner.class)
public class LotDetailsViewComponentTest {

	@InjectMocks
	LotDetailsViewComponent lotDetailsViewComponent = new LotDetailsViewComponent(ListInventoryDataInitializer.createLotDetail(1, 1));

	@Mock
	private InventoryDataManager inventoryDataManager;

	public static final String DATE_HEADER = "DATE";
	public static final String TYPE_HEADER = "TYPE";
	public static final String AMOUNT_HEADER = "AMOUNT";
	public static final String SEED_SOURCE_HEADER = "SEED SOURCE";
	public static final String LIST_NAME_HEADER = "LIST NAME";
	public static final String USER_HEADER = "USER";


	public static final String LIST_NAME = "List1";
	public static final String USER = "User";
	public static final String STATUS = "Active";


	public static final String LOT = "Lot 2";
	public static final String LOCATION = "Location2";
	public static final String SCALE = "Scale2";
	public static final String ACTUAL_BALANCE = "100.0g";
	public static final String AVAILABLE_BALANCE = "100.0g";

	@Before
	public void setUp() {
		this.lotDetailsViewComponent.instantiateComponents();
	}

	@Test
	public void testInstantiateComponents(){

		this.lotDetailsViewComponent.getLotIdLabel().toString();
		Assert.assertEquals(LOT, this.lotDetailsViewComponent.getLotIdLabel().toString());
		Assert.assertEquals(LOCATION, this.lotDetailsViewComponent.getLocation().toString());
		Assert.assertEquals(SCALE, this.lotDetailsViewComponent.getScale().toString());
		Assert.assertEquals(ACTUAL_BALANCE, this.lotDetailsViewComponent.getActualBalance().toString());
		Assert.assertEquals(AVAILABLE_BALANCE, this.lotDetailsViewComponent.getAvailableBalance().toString());

	}

	@Test
	public void testLotEntriesTablesColumnsAndHeaderNames() {

		Table table = this.lotDetailsViewComponent.getTable();
		Assert.assertNotNull(table);
		Collection<?> columnIds = table.getContainerPropertyIds();

		Assert.assertTrue(columnIds.size() == 6);
		Assert.assertTrue(columnIds.contains(LotDetailsViewComponent.DATE));
		Assert.assertTrue(columnIds.contains(LotDetailsViewComponent.TYPE));
		Assert.assertTrue(columnIds.contains(LotDetailsViewComponent.AMOUNT));
		Assert.assertTrue(columnIds.contains(LotDetailsViewComponent.SEED_SOURCE));
		Assert.assertTrue(columnIds.contains(LotDetailsViewComponent.LIST_NAME));
		Assert.assertTrue(columnIds.contains(LotDetailsViewComponent.USER));

		Assert.assertEquals(DATE_HEADER, table.getColumnHeader(lotDetailsViewComponent.DATE));
		Assert.assertEquals(TYPE_HEADER, table.getColumnHeader(LotDetailsViewComponent.TYPE));
		Assert.assertEquals(AMOUNT_HEADER, table.getColumnHeader(lotDetailsViewComponent.AMOUNT));
		Assert.assertEquals(SEED_SOURCE_HEADER, table.getColumnHeader(lotDetailsViewComponent.SEED_SOURCE));
		Assert.assertEquals(LIST_NAME_HEADER, table.getColumnHeader(lotDetailsViewComponent.LIST_NAME));
		Assert.assertEquals(USER_HEADER, table.getColumnHeader(lotDetailsViewComponent.USER));

	}

	@Test
	public void testinitializeValues() {

		Mockito.when(this.inventoryDataManager.getTransactionDetailsForLot(Mockito.anyInt()))
				.thenReturn(InventoryDetailsTestDataInitializer.createTransactionReportRowTestData());
		this.lotDetailsViewComponent.initializeValues();
		Item item = this.lotDetailsViewComponent.getTable().getItem(lotDetailsViewComponent.getTable().lastItemId());

		Assert.assertEquals(1, lotDetailsViewComponent.getTable().size());
		String dateString = (String) item.getItemProperty(LotDetailsViewComponent.DATE).getValue();
		Assert.assertEquals("Thu Dec 31 00:00:00 ART 2009", dateString);
		Assert.assertEquals(STATUS, item.getItemProperty(LotDetailsViewComponent.TYPE).getValue());
		Assert.assertEquals("-50.0", item.getItemProperty(LotDetailsViewComponent.AMOUNT).getValue().toString().replace("g", ""));
		Assert.assertEquals(LIST_NAME, item.getItemProperty(LotDetailsViewComponent.LIST_NAME).getValue());
		Assert.assertEquals(USER, item.getItemProperty(LotDetailsViewComponent.USER).getValue().toString());
	}

	@Test
	public void testLayoutComponents() {
		this.lotDetailsViewComponent.layoutComponents();
		Assert.assertEquals(5, this.lotDetailsViewComponent.getPanelContentLayout().getComponentCount());
	}


}
