
package org.generationcp.commons.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.generationcp.commons.constant.ListTreeState;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.UserProgramStateDataManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserTreeStateServiceImplTest {

	private static final Integer TEST_USER_ID = 2;
	private static final String TEST_PROGRAM_UUID = "test";
	private static final Integer FIRST_LEVEL_FOLDER_ID = 1;
	private static final Integer SECOND_LEVEL_FOLDER_ID = 2;
	private static final Integer TEST_LIST_ID = 3;

	@Mock
	private UserProgramStateDataManager userProgramStateDataManager;

	@Mock
	private GermplasmListManager germplasmListManager;

	@InjectMocks
	private UserTreeStateServiceImpl unitUnderTest;

	@Test
	public void testRetrieveSaveListNoItemsSaved() {
		List<String> savedNavigationState = new ArrayList<>();
		Mockito.when(germplasmListManager.getLastSavedGermplasmListByUserId(TEST_USER_ID, TEST_PROGRAM_UUID)).thenReturn(null);
		Mockito.when(
				userProgramStateDataManager.getUserProgramTreeStateByUserIdProgramUuidAndType(TEST_USER_ID, TEST_PROGRAM_UUID,
						ListTreeState.GERMPLASM_LIST.name())).thenReturn(savedNavigationState);

		List<String> retrievedState = unitUnderTest.getUserProgramTreeStateForSaveList(TEST_USER_ID, TEST_PROGRAM_UUID);
		Assert.assertEquals("Service must retrieve the latest tree navigation if no lists have been saved currently", savedNavigationState,
				retrievedState);
	}

	@Test
	public void testRetrieveSaveListItemsPreviouslySaved() {
		Mockito.when(germplasmListManager.getLastSavedGermplasmListByUserId(TEST_USER_ID, TEST_PROGRAM_UUID)).thenReturn(
				constructDummyNestedGermplasmListFolder());

		List<String> expectedTreeState =
				Arrays.asList(new String[] {UserTreeStateServiceImpl.GERMPLASM_LIST_ROOT_ITEM, FIRST_LEVEL_FOLDER_ID.toString(),
						SECOND_LEVEL_FOLDER_ID.toString()});

		List<String> actualState = unitUnderTest.getUserProgramTreeStateForSaveList(TEST_USER_ID, TEST_PROGRAM_UUID);

		Assert.assertEquals(
				"Service must generate a state starting from the list item name, followed by the folder IDs of the containing folders",
				expectedTreeState, actualState);
	}

	private GermplasmList constructDummyNestedGermplasmListFolder() {
		GermplasmList firstLevel = new GermplasmList(FIRST_LEVEL_FOLDER_ID);
		firstLevel.setType(GermplasmList.FOLDER_TYPE);
		GermplasmList secondLevel = new GermplasmList(SECOND_LEVEL_FOLDER_ID);
		secondLevel.setType(GermplasmList.FOLDER_TYPE);
		secondLevel.setParent(firstLevel);

		GermplasmList actualList = new GermplasmList(TEST_LIST_ID);
		actualList.setType(GermplasmList.LIST_TYPE);
		actualList.setParent(secondLevel);

		return actualList;
	}
}
